package pl.kawowydzienniczek.kawowydzienniczek.Services;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.LoginErrors;
import pl.kawowydzienniczek.kawowydzienniczek.Globals.User;

public class HttpService {

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


    public Boolean isRequestAuthorized(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        return obj.optString("detail").isEmpty();
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

    //different kind of response data
    public  class ProductData{

        private String id;
        private String name;
        private String description;
        private String price;
        private String img;;

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
