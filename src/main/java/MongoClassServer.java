import com.google.gson.*;
import com.mongodb.*;
import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * Created by Adam on 6/17/15.
 */
public class MongoClassServer {
    private MongoClient mongoClient;
    DBCollection collection;

    public MongoClassServer() {
        mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("NEUClass");
        collection = db.getCollection("Fall");
    }


    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MongoHandler());
        server.setExecutor(null);
        server.start();
    }

    public static void main(String[] args) throws Exception {
        MongoClassServer mongoClassServer = new MongoClassServer();
        mongoClassServer.start();
    }


    static JsonParser jsonParser = new JsonParser();

    private class MongoHandler implements HttpHandler {



        private String find(String request) {

            try {

                JsonArray jsonArray = new JsonArray();

                DBObject dbObject = (DBObject) JSON.parse(request);
                DBCursor cursor = collection.find(dbObject);

                while (cursor.hasNext()) {
                    DBObject obj = cursor.next();


                    JsonObject jsonObject = (JsonObject) jsonParser.parse(obj.toString());
                    jsonArray.add(jsonObject);
                }

                return jsonArray.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "[]";
            }

        }

        public void handle(HttpExchange httpExchange) throws IOException {
            InputStream inputStream = httpExchange.getRequestBody();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder inputBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                inputBuilder.append(line);
            }

            bufferedReader.close();

            String response = find(inputBuilder.toString());
            httpExchange.setAttribute("Content-type", "application/json");
            httpExchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }
    }
}
