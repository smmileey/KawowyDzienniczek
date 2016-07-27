package pl.kawowydzienniczek.kawowydzienniczek.Data;

public class ProductData{

    private String id;
    private String name;
    private String description;
    private String price;
    private String img;

    public  ProductData(String id, String name, String description, String price, String img){
        this.id = id;
        this. name = name;
        this.description=description;
        this.price = price;
        this.img=img;
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

    public String getImg() {
        return img;
    }
}