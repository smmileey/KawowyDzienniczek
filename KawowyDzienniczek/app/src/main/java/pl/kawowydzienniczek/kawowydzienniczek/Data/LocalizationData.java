package pl.kawowydzienniczek.kawowydzienniczek.Data;

public class LocalizationData {

    private String url;
    private String id;
    private String latitude;
    private String longitude;
    private String city;
    private String road;
    private String road_number;

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getRoad() {
        return road;
    }

    public String getRoad_number() {
        return road_number;
    }

    public LocalizationData(String url, String id, String latitude, String longitude, String city,
                            String road, String road_number){
        this.url = url;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city= city;
        this.road = road;
        this.road_number = road_number;


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizationData that = (LocalizationData) o;

        if (!url.equals(that.url)) return false;
        if (!id.equals(that.id)) return false;
        if (!latitude.equals(that.latitude)) return false;
        if (!longitude.equals(that.longitude)) return false;
        if (!city.equals(that.city)) return false;
        if (!road.equals(that.road)) return false;
        return road_number.equals(that.road_number);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + road.hashCode();
        result = 31 * result + road_number.hashCode();
        return result;
    }
}