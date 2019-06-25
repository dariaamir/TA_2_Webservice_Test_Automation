package responses;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Posts {

    public Posts(){};

    public Posts(String userId, String title, String body){
        this.userId = userId;
        this.id = 1;
        this.title = title;
        this.body = body;
    };

    private String userId;
    private int id;
    private String title;
    private String body;

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getBody() {return body;}

    public void setBody (String body) {this.body = body;}
}
