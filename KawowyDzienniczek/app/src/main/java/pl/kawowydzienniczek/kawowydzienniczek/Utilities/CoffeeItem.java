package pl.kawowydzienniczek.kawowydzienniczek.Utilities;

public class CoffeeItem {
    private String title;
    private String description;

    public CoffeeItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
