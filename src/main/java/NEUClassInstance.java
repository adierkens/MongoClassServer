import com.google.gson.Gson;
import neu_class.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam on 6/9/15.
 */
public class NEUClassInstance {
    private Term term;
    private Level level;
    private List<String> attributes;
    private String instructor;
    private ScheduleType scheduleType;
    private String title;
    private Integer crn;
    private Subject subject;
    private Integer courseNumber;
    private List<MeetingTime> meetingTimes;

    private static final Gson gson = new Gson();

    public String toJsonString() {
        return gson.toJson(this);
    }

    public Term getTerm() {
        return term;
    }

    public NEUClassInstance() {
        meetingTimes = new ArrayList<MeetingTime>();
        attributes = new ArrayList<String>();
    }

    public NEUClassInstance setTerm(Term term) {
        this.term = term;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    public NEUClassInstance setLevel(Level level) {
        this.level = level;
        return this;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public NEUClassInstance setAttributes(List<String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String getInstructor() {
        return instructor;
    }

    public NEUClassInstance setInstructor(String instructor) {
        this.instructor = instructor;
        return this;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public NEUClassInstance setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder meetingBuilder = new StringBuilder();
        String prefix = "";

        for (MeetingTime m : meetingTimes) {
            meetingBuilder.append(prefix);
            meetingBuilder.append(m.toString());
            prefix = ", ";
        }

        return String.format(Locale.US, "Term: %s, Level: %s, Attributes: %s, Instructor: %s, Type: %s, Title: %s, CRN: %s, Subject: %s, CourseNum: %s, Times: %s",
                term, level, attributes, instructor, scheduleType, title, crn, subject, courseNumber, meetingBuilder.toString());
    }

    public String getTitle() {
        return title;
    }

    public NEUClassInstance setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getCrn() {
        return crn;
    }

    public NEUClassInstance setCrn(Integer crn) {
        this.crn = crn;
        return this;
    }

    public Subject getSubject() {
        return subject;
    }

    public NEUClassInstance setSubject(Subject subject) {
        this.subject = subject;
        return this;
    }

    public Integer getCourseNumber() {
        return courseNumber;
    }

    public NEUClassInstance setCourseNumber(Integer courseNumber) {
        this.courseNumber = courseNumber;
        return this;
    }

    public List<MeetingTime> getMeetingTimes() {
        return meetingTimes;
    }

    public void setMeetingTimes(List<MeetingTime> meetingTimes) {
        this.meetingTimes = meetingTimes;
    }
}
