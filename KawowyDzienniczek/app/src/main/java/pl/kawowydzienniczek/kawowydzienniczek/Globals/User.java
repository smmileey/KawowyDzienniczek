package pl.kawowydzienniczek.kawowydzienniczek.Globals;


public class User {

    private String url;
    private int id;
    private String username;
    private String email;

    public User(String url, int id, String username, String email){
        this.url = url;
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
