package pl.kawowydzienniczek.kawowydzienniczek.Data;

public class UserData{

    private String url;
    private int id;
    private User user;
    private String photo;

    public UserData(String url, int id, User user, String photo){
        this.url = url;
        this.id = id;
        this.user = user;
        this.photo = photo;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (id != userData.id) return false;
        if (!url.equals(userData.url)) return false;
        if (!user.equals(userData.user)) return false;
        return photo.equals(userData.photo);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + id;
        result = 31 * result + user.hashCode();
        result = 31 * result + photo.hashCode();
        return result;
    }
}
