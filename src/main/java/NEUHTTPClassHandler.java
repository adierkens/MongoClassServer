import com.mongodb.*;
import com.mongodb.util.JSON;
import neu_class.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adam on 6/9/15.
 */
public class NEUHTTPClassHandler {
    static String pattern = "([\\s\\w\\(\\),-]+)\\s-\\s(\\d+)\\s-\\s(\\w+)\\s(\\d+)\\s-\\s(\\d+)-\\s(.+)\\s-\\sCredits\\s(\\d+)";
    static Pattern p = Pattern.compile(pattern);

    static MongoClient mongoClient = new MongoClient("localhost");

    public List<NEUClassInstance> getClasses(Term term, Subject subject) {
        return parse(sendRequest(generateRequest(term, subject)));
    }

    private static List<NEUClassInstance> parse(String httpResponse) {
        List<NEUClassInstance> classInstances = new ArrayList<NEUClassInstance>();

        return classInstances;
    }

    private String sendRequest(String request) {
        return "";
    }

    private String generateRequest(Term term, Subject subject){
        return "";
    }

    private static NEUClassInstance parseElement(Element e, Element headerElement) {

        NEUClassInstance neuClassInstance = new NEUClassInstance();

        List<TextNode> tmpNodes = e.textNodes();
        List<TextNode> nodes = new ArrayList<TextNode>();

        for (int i=0; i<tmpNodes.size(); i++) {
            if (i == 0 || i == 1) {
                if (e.text().startsWith("Please")) {
                    continue;
                }
            }

            nodes.add(tmpNodes.get(i));
        }

        if (nodes.size() < 5) return null;

        Elements spanElements = e.getElementsByClass("fieldlabeltext");

        if (headerElement != null) {
            Matcher m = p.matcher(headerElement.text());
            if (m.find()) {
                neuClassInstance
                        .setTitle(m.group(1))
                        .setCrn(Integer.parseInt(m.group(2)))
                        .setSubject(Subject.valueOf(m.group(3)))
                        .setCourseNumber(Integer.parseInt(m.group(4)));
            } else {
                System.out.println("Regex doesn't match");
            }
        } else {
            System.out.println("Header element is null");
        }

        for (int i=0; i<spanElements.size(); i++) {
            Element span = spanElements.get(i);

            try {
                if (span.text().startsWith("Associated")) {
                    neuClassInstance.setTerm(Term.valueOf(nodes.get(i).text().split(" ")[0]));
                } else if (span.text().startsWith("Levels")) {
                    String txt = nodes.get(i).text().trim();
                    if (txt.contains("Graduate") && txt.contains("Undergraduate")) {
                        neuClassInstance.setLevel(Level.Both);
                    } else {
                        neuClassInstance.setLevel(Level.valueOf(nodes.get(i).text().trim()));
                    }
                } else if (span.text().startsWith("Attributes")) {

                } else if (span.text().equals("Schedule Type: ")) {

                } else if (span.text().startsWith("Instructors")) {
                    neuClassInstance.setInstructor(nodes.get(i).text().split("\\(")[0].trim());
                }
            } catch (Exception except) {
                //System.out.println(nodes.get(i).text());
                except.printStackTrace();
            }
        }


        Elements scheduleTable = e.getElementsByTag("TR");

        if (scheduleTable.size() > 0) {

            Element scheduleHeader = scheduleTable.get(0);


            List<MeetingTime> meetingTimes = new ArrayList<MeetingTime>();

            for (int i = 1; i < scheduleTable.size(); i++) {
                Element timeEle = scheduleTable.get(i);

                MeetingTime meetingTime = new MeetingTime();


                for (int j = 0; j < scheduleHeader.children().size(); j++) {

                    String key = scheduleHeader.child(j).text();
                    String value = timeEle.child(j).text().trim();
                    value = value.replaceAll("\u00A0", "");

                    if (value.equals("") || value.equals("N/A")) {
                        continue;
                    }

                    try {
                        if (key.equals("Type")) {
                            if (value.equals("Class")) {
                                meetingTime.setType(Type.Class);
                            } else if (value.equals("Final Exam")) {
                                meetingTime.setType(Type.FinalExam);
                            }
                        } else if (key.equals("Time")) {
                            meetingTime.parseTimeRange(value);
                        } else if (key.equals("Days")) {
                            meetingTime.parseDays(value);
                        } else if (key.equals("Where")) {
                            meetingTime.setWhere(value);
                        } else if (key.equals("Date Range")) {

                        } else if (key.equals("Capacity")) {
                            meetingTime.setCapacity(Integer.parseInt(value));
                        } else if (key.equals("Actual")) {
                            meetingTime.setActual(Integer.parseInt(value));
                        } else if (key.equals("WL Act")) {
                            //
                        } else if (key.equals("Seats")) {
                            meetingTime.setSeats(Integer.parseInt(value));
                        } else if (key.equals("Room Size")) {
                            meetingTime.setRoomSize(Integer.parseInt(value));
                        }
                    } catch (Exception exc) {
                        System.out.println(String.format("Key: %s, Val: %s", key, value));
                        exc.printStackTrace();
                    }
                }

                meetingTimes.add(meetingTime);
            }

            neuClassInstance.setMeetingTimes(meetingTimes);

        }
        return neuClassInstance;
    }

    public static void main(String[] args) throws Exception {

        for (Subject s : Subject.values()) {

            String postData = "sel_day=dummy&STU_TERM_IN=201610&sel_subj=dummy&sel_attr=dummy&sel_schd=dummy&sel_camp=dummy&sel_insm=dummy&sel_ptrm=dummy&sel_levl=dummy&sel_instr=dummy&sel_seat=dummy&p_msg_code=UNSECURED&sel_crn=&sel_subj=" +
                    s.toString() +
                    "&sel_crse=&sel_title=&sel_attr=%25&sel_levl=%25&sel_schd=%25&sel_insm=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a";
            URLConnection uc = new URL("https://wl11gp.neu.edu/udcprod8/NEUCLSS.p_class_search").openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setAllowUserInteraction(false);
            DataOutputStream dos = new DataOutputStream(uc.getOutputStream());

            dos.writeBytes(postData);
            dos.close();

            DataInputStream dis = new DataInputStream(uc.getInputStream());
            StringBuilder responseBuilder = new StringBuilder();
            String nxtLine;
            while ((nxtLine = dis.readLine()) != null) {
                responseBuilder.append(nxtLine);
            }
            dis.close();

            Document doc = Jsoup.parse(responseBuilder.toString());
            Elements elements = doc.getElementsByTag("TD");
            Elements headerElements = doc.getElementsByTag("TH");

            List<Element> headers = new ArrayList<Element>();

            for (Element header : headerElements) {
                if (p.matcher(header.text()).find()) {
                    headers.add(header);
                }
            }

            List<Element> otherElements = new ArrayList<Element>();
            for (Element element : elements) {
                if (element.hasClass("dddefault")) {
                    if (element.children().size() > 0) {
                        if (element.child(0).hasClass("fieldlabeltext") || element.text().startsWith("Please")) {
                            otherElements.add(element);
                        }
                    }
                }
            }

            System.out.println("Header size: " + headers.size() + " other size: " + otherElements.size());

            for (int i = 0; i < headers.size(); i++) {
                Element other = otherElements.get(i);
                Element header = headers.get(i);

                NEUClassInstance neu = parseElement(other, header);
                if (neu != null) {
                    store(neu);
                }

            }
        }
    }

    private static void store(NEUClassInstance instance) {
        DB db = mongoClient.getDB("NEUClass");
        DBCollection dbCollection = db.getCollection("Fall");

        DBObject dbObject = (DBObject) JSON.parse(instance.toJsonString());

        if (dbObject.containsKey("crn")) {
            dbObject.put("_id", dbObject.get("crn"));
        }

        dbCollection.insert(dbObject);
    }
}
