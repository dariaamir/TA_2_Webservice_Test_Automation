package support;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.JsonPath;
import responses.Comments;
import responses.Posts;
import responses.Todos;
import org.json.JSONObject;


public class RequestSender {

    public RequestSender(){};
    private static int responseCode;
    private static String responseBody;

    public static void requestSend(String callMethod, String callURL, String callBody) throws IOException {
        URL url = new URL(callURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (callMethod.equals("PATCH")){
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestMethod("POST");
        }
        else {
            connection.setRequestMethod(callMethod);
        }

        if (!callBody.equals("")){
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(callBody);
        }

        responseCode = connection.getResponseCode();

        responseBody = "";
        if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                responseBody += inputLine + "\n";
            }
        }
        System.out.println(responseBody);
    }

    public static int getResponseCodeFromCall(){
        return responseCode;
    }

    public static String getResponseBodyFromCall(){
        return responseBody;
    }

    public static String getResponseBodyElementFromCall(String element){
        JSONObject responseBodyJSON = new JSONObject(responseBody);
        return responseBodyJSON.get(element).toString();
    }

    public static int getResponseBodyNumberOfElementsFromCall(String searchString){
        List <String> array = JsonPath.parse(responseBody).read(searchString);
        return array.size();
    }

    public static HttpURLConnection establishCall(String method, String url) throws IOException{
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod(method);
        return connection;
    }

    public static int get_responce_code(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode();
    }

    public static String get_data_from_post(String datatype, HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        ObjectMapper mapper = new ObjectMapper();
        Posts post = mapper.readValue(br, Posts.class);
        String response = "";

        switch (datatype) {
            case "author":
                response = String.valueOf(post.getUserId());
                break;
            case "title":
                response = post.getTitle();
                break;
            case "body":
                String body = post.getBody();
                response = (body.contains("\n")) ? response = body.replace("\n"," ") : body;
                break;
            case "id":
                response = String.valueOf(post.getId());
                break;
            case "all":
                String author = String.valueOf(post.getUserId());
                String title = post.getTitle();
                body = post.getBody();
                body = (body.contains("\n")) ? body = body.replace("\n"," ") : body;
                response = author + " " + title + " " + body;
        }
        return response;
    }

    public static String get_data_from_comment(String datatype, HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        ObjectMapper mapper = new ObjectMapper();
        Comments comment = mapper.readValue(br, Comments.class);
        String response = "";

        switch (datatype) {
            case "postId":
                response = String.valueOf((comment.getPostId()));
                break;
            case "name":
                response = comment.getName();
                break;
            case "email":
                response = comment.getEmail();
                break;
            case "body":
                response = comment.getBody().replace("\n"," ");
                break;
        }
        return response;
    }

    public static int get_data_from_todos_group(String datatype, HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        ObjectMapper mapper = new ObjectMapper();
        Todos[] groupOfTodos = mapper.readValue(br, Todos[].class);
        int response = 0;
        switch (datatype) {
            case "all_todos_count":
                response = groupOfTodos.length;
                break;
            case "all_completed_todos_count":
                int count = 0;
                for(Todos todo: groupOfTodos){
                    if (todo.getCompleted()){
                        count++;
                    }
                }
                response = count;
                break;
        }
        return response;
    }

    public static String get_data_from_todos(String datatype, HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        ObjectMapper mapper = new ObjectMapper();
        Todos todo = mapper.readValue(br, Todos.class);
        String response = "";

        switch (datatype) {
            case "status":
                response = String.valueOf((todo.getCompleted()));
                break;
        }
        return response;
    }

    public static void writePost(HttpURLConnection connection, String title, String body, String userId) throws IOException{
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        Posts post = new Posts(userId = userId, title = title, body = body);
        String json = ow.writeValueAsString(post);
        os.write(json.getBytes("UTF-8"));
        os.close();
    }

    public static void patchPost(HttpURLConnection connection, String old_userID, String old_title, String old_body, String post_field, String new_value) throws IOException{
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Posts post = new Posts(old_userID, old_title, old_body);

        switch (post_field){
            case "userID":
                post.setUserId(new_value);
                break;
            case "title":
                post.setTitle(new_value);
                break;
            case "body":
                post.setTitle(new_value);
                break;
        }

        String json = ow.writeValueAsString(post);
        os.write(json.getBytes("UTF-8"));
        os.close();
    }
}

