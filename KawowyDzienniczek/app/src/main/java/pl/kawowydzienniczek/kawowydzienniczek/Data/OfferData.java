package pl.kawowydzienniczek.kawowydzienniczek.Data;

public class OfferData{

    private String id;
    private String name;
    private String description;
    private String price;
    private String image;

    public OfferData(String id, String name, String description, String price, String image){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfferData offerData = (OfferData) o;

        if (!id.equals(offerData.id)) return false;
        if (!name.equals(offerData.name)) return false;
        if (!description.equals(offerData.description)) return false;
        if (!price.equals(offerData.price)) return false;
        return image.equals(offerData.image);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + image.hashCode();
        return result;
    }
}