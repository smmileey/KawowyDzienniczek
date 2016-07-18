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
import pl.kawowydzienniczek.kawowydzienniczek.Constants.LoginErrors;
import pl.kawowydzienniczek.kawowydzienniczek.Globals.User;

public class KawowyDzienniczekService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String postRequest(String url, String params, String auth) throws IOException {
        RequestBody body = RequestBody.create(JSON, params);
        Request.Builder builder = new Request.Builder().url(url);

        if(auth != null)
            builder = builder.addHeader("Authorization", "Token " + auth);
        Request request = builder
                .post(body)
                .build();
        try(Response response = client.newCall(request).execute()){
            return response.body().string();
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
            return response.body().string();
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
            return response.body().string();
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
        return obj.optString("detail").isEmpty();
    }

    public String makeJsonUsername(String email, String password) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("email",email);
        object.put("password", password);
        return object.toString();
    }

    public ProductData getProductData(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        return  new ProductData(json.getString("id"),
                json.getString("name"),
                json.getString("description"),
                json.getString("price"),
                json.getString("img"));
    }

    boolean isDateWithinRange(Date start, Date end, Date testDate) {
        if(testDate == null)
            return false;

        if(start == null){
            return end == null || !(testDate.after(end)); //always-on
        }

        if(end == null){
            return !(testDate.before(start));
        }
        return !(testDate.before(start) || testDate.after(end));
    }

    public void getPromotionDataByStatusReplaceExistingList(List<PromotionData> existing, String response, String status) throws JSONException, ParseException {
        JSONObject json = new JSONObject(response);
        JSONArray promotions = json.getJSONArray("results");
        for(int i=0;i<promotions.length();i++){
            JSONObject temp = promotions.getJSONObject(i);

            if(temp.getString("status").equals(status)) {
                JSONObject singleProm = temp.getJSONObject("promotion");
                String startDate = singleProm.getString("start_date"),
                        endDate= singleProm.getString("end_date"),
                        format = "yyyy-MM-dd";
                SimpleDateFormat dFormat = new SimpleDateFormat(format);
                Date sDate = startDate.equals("null") ? null: dFormat.parse(startDate);
                Date eDate = endDate.equals("null")? null: dFormat.parse(endDate);
                Date todayDate = Calendar.getInstance().getTime();

                if(isDateWithinRange(sDate, eDate, todayDate)) {
                    existing.add(getSinglePromotionData(singleProm,sDate,eDate));
                }
            }
        }
    }

    public void getAvailablePromotionDataReplaceExistingList(List<PromotionData> existing, String response) throws JSONException, ParseException {
        existing.clear();

        JSONObject base = new JSONObject(response);
        JSONObject promotionTag = base.getJSONObject("promotion");
        JSONArray promotions = promotionTag.getJSONArray("promotions");
        JSONObject tempJson;

        String startDate,endDate, format = "yyyy-MM-dd";
        SimpleDateFormat dFormat = new SimpleDateFormat(format);

        for(int i=0;i<promotions.length();i++){
            tempJson = promotions.getJSONObject(i);
            startDate = tempJson.getString("start_date");
            endDate = tempJson.getString("end_date");
            Date sDate = startDate.equals("null") ? null: dFormat.parse(startDate);
            Date eDate = endDate.equals("null")? null: dFormat.parse(endDate);
            Date todayDate = Calendar.getInstance().getTime();

            if(isDateWithinRange(sDate, eDate, todayDate)) {
                existing.add(getSinglePromotionData(tempJson,sDate,eDate));
            }
        }
    }

    //deprecated HEHE
    public List<PromotionData> getAvailablePromotionsDataReturnNewList(String response, int coffeeShopId) throws JSONException, ParseException {
        List<PromotionData> promData = new ArrayList<>();
        JSONObject base = new JSONObject(response);
        JSONArray results = base.getJSONArray("results");
        JSONObject specfificOffer = results.getJSONObject(coffeeShopId - 1);
        JSONArray promotions = specfificOffer.getJSONArray("promotions");
        JSONObject tempJson;
        String startDate,endDate, format = "yyyy-MM-dd";
        SimpleDateFormat dFormat = new SimpleDateFormat(format);

        for(int i=0;i<promotions.length();i++) {
            tempJson = promotions.getJSONObject(i);
            startDate = tempJson.getString("start_date");
            endDate = tempJson.getString("end_date");
            Date sDate = startDate.equals("null") ? null : dFormat.parse(startDate);
            Date eDate = endDate.equals("null") ? null : dFormat.parse(endDate);
            Date todayDate = Calendar.getInstance().getTime();

            if (isDateWithinRange(sDate, eDate, todayDate)) {
                promData.add(getSinglePromotionData(tempJson,sDate,eDate));
            }
        }
        return promData;
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
                json.getString("id"),
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

    public CoffeePreviewData getCoffeePreviewData(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        return  new CoffeePreviewData(
                json.getString("name"),
                json.getString("localization"),
                json.getString("logo_img"),
                json.getString("screen_img"),
                json.getString("description"),
                json.getString("type"),
                json.getString("offer"),
                json.getString("promotion"),
                json.getString("id"));
    }


    public class PromotionData{

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
            this. start_date = start_date;
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
    }

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

    public class CoffeePreviewData {

        private String name;
        private String localization;
        private String image_logo;
        private String image_screen;
        private String desc;
        private String type;
        private String offer;
        private String promotion;
        private String id;

        public CoffeePreviewData(String name, String localization, String image_logo, String image_screen, String desc,
                                 String type, String offer, String promotion, String id){
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

        public String getLocalization() {
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

        public String getOffer() {
            return offer;
        }

        public String getPromotion() {
            return promotion;
        }
    }

}
