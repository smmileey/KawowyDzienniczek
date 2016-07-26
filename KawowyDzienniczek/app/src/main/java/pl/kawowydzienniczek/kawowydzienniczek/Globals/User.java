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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (!url.equals(user.url)) return false;
        if (!username.equals(user.username)) return false;
        return email.equals(user.email);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + id;
        result = 31 * result + username.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
