package pl.kawowydzienniczek.kawowydzienniczek.Data;

import java.util.List;

public class CoffeePreviewData {

    private String name;
    private LocalizationData localization;
    private String image_logo;
    private String image_screen;
    private String desc;
    private String type;
    private List<OfferData> offer;
    private List<PromotionData> promotion;
    private String id;

    public CoffeePreviewData(String name, LocalizationData localization, String image_logo, String image_screen, String desc,
                             String type, List<OfferData> offer, List<PromotionData> promotion, String id){
        this.name = name;
        this.localization = localization;
        this.image_logo= image_logo;
        this.image_screen = image_screen;
        this.desc = desc;
        this.type = type;
        this.offer = offer;
        this.promotion = promotion;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public LocalizationData getLocalization() {
        return localization;
    }

    public String getImage_logo() {
        return image_logo;
    }

    public String getImage_screen() {
        return image_screen;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public List<OfferData> getOffer() {
        return offer;
    }

    public List<PromotionData> getPromotion() {
        return promotion;
    }
}
