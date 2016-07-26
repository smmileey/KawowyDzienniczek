package pl.kawowydzienniczek.kawowydzienniczek.Services;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.LoginErrors;
import pl.kawowydzienniczek.kawowydzienniczek.Globals.User;

public class KawowyDzienniczekService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String postRequestWithParameters(String url, String params, String auth) throws IOException {
        RequestBody body = RequestBody.create(JSON, params);
        Request.Builder builder = new Request.Builder().url(url);

        if(auth != null)
            builder = builder.addHeader("Authorization", "Token " + auth);
        Request request = builder.post(body).build();

        try(Response response = client.newCall(request).execute()){
            return response == null? "Response is null." : response.body().string();
        }
    }

    public String getRequest(String url, String auth) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);

        if(auth != null)
            builder = builder.addHeader("Authorization", "Token " + auth);
        Request request = builder
                .get()
                .build();
        try(Response response = client.newCall(request).execute()){
            return response==null? "Response is null." : response.body().string();
        }
    }

    public String getRequestWithParameters(String url, String auth, Map<String,String> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> param: params.entrySet()) {
            urlBuilder = urlBuilder.addQueryParameter(param.getKey(),param.getValue());
        }
        String parametrizedUrl = urlBuilder.build().toString();
        Request.Builder builder = new Request.Builder().url(parametrizedUrl);

        if(auth != null)
            builder = builder.addHeader("Authorization", "Token " + auth);
        Request request = builder
                .get()
                .build();
        try(Response response = client.newCall(request).execute()){
            return response==null? "Response is null." : response.body().string();
        }
    }

    public LoginResponseData getToken(String response) throws JSONException {
        JSONObject resJson = new JSONObject(response);
        HashMap<LoginErrors,String> hMap = new HashMap<>();
        try {
            String token = resJson.getString("token");
            return new LoginResponseData(true, token, hMap);
        }
        catch (JSONException ex){
            String error = resJson.optString("non_field_errors");
            hMap.put(LoginErrors.GENERAL,error);
            return new LoginResponseData(false, "none",hMap);
        }
    }

    public Boolean isRequestAuthorized(String response) throws JSONException {
        if(response == null)
            return false;
        JSONObject obj = new JSONObject(response);
        return !obj.has("detail");
    }

    public String makeJsonUsernameCredentials(String email, String password) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("email",email);
        object.put("password", password);
        return object.toString();
    }

    public ProductData getProductData(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        return  new ProductData(json.optString("id"),
                json.optString("name"),
                json.optString("description"),
                json.optString("price"),
                json.optString("img"));
    }

    public boolean isDateWithinRange(Date start, Date end, Date testDate) {
        if(testDate == null)
            return false;

        if(start == null){
            return end == null || !(testDate.after(end)); // end==null -> prom is always-on
        }

        if(end == null){
            return !(testDate.before(start));
        }
        return !(testDate.before(start) || testDate.after(end));
    }

    public void getPromotionDataByStatusReplaceExistingList(List<PromotionData> existing, String response,
                                                            List<String> statuses) throws JSONException, ParseException {
        if(existing != null) {
            JSONObject json = new JSONObject(response);
            JSONArray promotions = json.getJSONArray("results");
            for (int i = 0; i < promotions.length(); i++) {
                JSONObject temp = promotions.getJSONObject(i);

                if (statuses.contains(temp.getString("progress"))) {  //na przykład chcemy promocje z progressem: AC lub AV
                    JSONObject singleProm = temp.getJSONObject("promotion");
                    String startDate = singleProm.getString("start_date"),
                            endDate = singleProm.getString("end_date"),
                            format = "yyyy-MM-dd";
                    SimpleDateFormat dFormat = new SimpleDateFormat(format);
                    Date sDate = startDate.equals("null") ? null : dFormat.parse(startDate);
                    Date eDate = endDate.equals("null") ? null : dFormat.parse(endDate);
                    Date todayDate = Calendar.getInstance().getTime();

                    if (isDateWithinRange(sDate, eDate, todayDate)) {
                        existing.add(getSinglePromotionData(singleProm, sDate, eDate));
                    }
                }
            }
        }
    }

    public void getAvailablePromotionDataReplaceExistingList(List<PromotionData> existing, String response) throws JSONException, ParseException {
        if(existing != null) {

            existing.clear();
            JSONObject base = new JSONObject(response);
            JSONObject promotionTag = base.getJSONObject("promotion");
            JSONArray promotions = promotionTag.getJSONArray("promotions");
            JSONObject tempJson;

            String startDate, endDate, format = "yyyy-MM-dd";
            SimpleDateFormat dFormat = new SimpleDateFormat(format);

            for (int i = 0; i < promotions.length(); i++) {
                tempJson = promotions.getJSONObject(i);
                if (tempJson.optString("status").equals(GeneralConstants.PROMOTION_STATUS_AVAILABLE)) {
                    startDate = tempJson.getString("start_date");
                    endDate = tempJson.getString("end_date");
                    Date sDate = startDate.equals("null") ? null : dFormat.parse(startDate);
                    Date eDate = endDate.equals("null") ? null : dFormat.parse(endDate);
                    Date todayDate = Calendar.getInstance().getTime();

                    if (isDateWithinRange(sDate, eDate, todayDate)) {
                        existing.add(getSinglePromotionData(tempJson, sDate, eDate));
                    }
                }
            }
        }
    }

    public PromotionData getSinglePromotionData(String response) throws JSONException, ParseException {
        JSONObject json = new JSONObject(response);
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = json.getString("start_date");
        String endDate = json.getString("end_date");
        Date sDate = startDate.equals("null") ? null : dFormat.parse(startDate);
        Date eDate = endDate.equals("null") ? null : dFormat.parse(endDate);
        Date todayDate = Calendar.getInstance().getTime();

        if(isDateWithinRange(sDate,eDate,todayDate)) {
            return getSinglePromotionData(json,sDate,eDate);
        }
        else {
            return null;
        }
    }

    private PromotionData getSinglePromotionData(JSONObject json, Date startDate, Date endDate) throws JSONException, ParseException {
        return new PromotionData(
                String.valueOf(json.getInt("id")),
                json.getString("name"),
                json.getString("description"),
                json.getString("code"),
                json.getString("img"),
                json.getString("status"),
                json.getString("left_number"),
                startDate,
                endDate);
    }

    public List<OfferData> getOfferData(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        JSONArray jArray = json.getJSONArray("products");
        List<OfferData> result = new ArrayList<>();

        for(int i=0;i<jArray.length();i++){
            JSONObject tempObject = jArray.getJSONObject(i);
            result.add(new OfferData(tempObject.getString("id"),
                    tempObject.getString("name"),
                    tempObject.getString("description"),
                    tempObject.getString("price"),
                    tempObject.getString("img")));
        }
        return result;

    }

    public UserData getUserData(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        JSONObject user = json.getJSONObject("user");
        return new UserData(json.getString("url"),
                json.getInt("id"),
                new User(user.getString("url"),user.getInt("id"),user.getString("username"),user.getString("email")),
                json.getString("photo"));
    }

    public CoffeePreviewData getCoffeePreviewData(String response) throws JSONException, ParseException {
        JSONObject json = new JSONObject(response);
        JSONObject tempJson = json.getJSONObject("localization");
        List<OfferData> offerDataList = new ArrayList<>();
        List<PromotionData> promotionDataList = new ArrayList<>();

        LocalizationData localizationData = new LocalizationData(
                tempJson.getString("url"),
                tempJson.getString("id"),
                tempJson.getString("latitude"),
                tempJson.getString("longitude"),
                tempJson.getString("city"),
                tempJson.getString("road"),
                tempJson.getString("road_number")
        );

        OfferData offerData = null;
        tempJson = json.getJSONObject("offer");
        JSONArray offersArray = tempJson.getJSONArray("products");
        for(int i=0;i<offersArray.length();i++){
            JSONObject tempObj = offersArray.getJSONObject(i);
            offerData = new OfferData(
                   tempObj.getString("id"),
                    tempObj.getString("name"),
                    tempObj.getString("description"),
                    tempObj.getString("price"),
                    tempObj.getString("img"));
            offerDataList.add(offerData);
        }

        PromotionData promData = null;
        tempJson = json.getJSONObject("promotion");
        JSONArray promArray = tempJson.getJSONArray("promotions");
        for(int i=0;i<promArray.length();i++){
            JSONObject tempObj = promArray.getJSONObject(i);
            promData = getSinglePromotionData(tempObj.toString());
            promotionDataList.add(promData);
        }

        return  new CoffeePreviewData(
                json.getString("name"),
                localizationData,
                json.getString("logo_img"),
                json.getString("screen_img"),
                json.getString("description"),
                json.getString("type"),
                offerDataList,
                promotionDataList,
                json.getString("id"));
    }

    public static class PromotionData{

        private String id;
        private String name;
        private String description;
        private String code;
        private String image;
        private String status;
        private String left_number;
        private Date start_date;
        private Date end_date;

        public PromotionData(String id, String name, String description, String code, String image, String status,
                             String left_number, Date start_date, Date end_date){
            this.id = id;
            this.name = name;
            this.description = description;
            this.code = code;
            this.image = image;
            this.status = status;
            this.left_number = left_number;
            this.start_date = start_date;
            this.end_date = end_date;
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

        public String getCode() {
            return code;
        }

        public String getImage() {
            return image;
        }

        public String getStatus() {
            return status;
        }

        public String getLeft_number() {
            return left_number;
        }

        public Date getStart_date() {
            return start_date;
        }

        public Date getEnd_date() {
            return end_date;
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PromotionData that = (PromotionData) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    //different kind of response data
    public  class ProductData{

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


    public static class OfferData{

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

    public static class UserData{

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

    public class LoginResponseData {

        private String token;
        private boolean isValid;
        private HashMap<LoginErrors, String> errors;

        public LoginResponseData(boolean valid, String tkn, HashMap<LoginErrors, String> err){
            token = tkn;
            errors = err;
            isValid = valid;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getToken() {
            return token;
        }

        public HashMap<LoginErrors,String> getErrors() {
            return errors;
        }
    }

    public static class CoffeePreviewData {

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

    public static class LocalizationData {

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
}
