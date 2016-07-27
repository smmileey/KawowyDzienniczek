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
import pl.kawowydzienniczek.kawowydzienniczek.Data.CoffeeAvailableData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.CoffeePreviewData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.LocalizationData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.LoginData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.OfferData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.ProductData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.PromotionData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.User;
import pl.kawowydzienniczek.kawowydzienniczek.Data.UserData;

public class KawowyDzienniczekService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    //post/get
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

    //functional methods
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

    public String getFormattedLocalization(LocalizationData localizationData){
        return localizationData.getCity()+", "+localizationData.getRoad()+" "+
                localizationData.getRoad_number();
    }

    public <T> Boolean copyArrayListByValue(List<T> source, List<T> destination){
        if(destination == null)
            return false;

        destination.clear();
        for (T item:source) {
            destination.add(item);
        }
        return true;
    }

    //data retrieve methods
    public LoginData getToken(String response) throws JSONException {
        JSONObject resJson = new JSONObject(response);
        HashMap<LoginErrors,String> hMap = new HashMap<>();
        try {
            String token = resJson.getString("token");
            return new LoginData(true, token, hMap);
        }
        catch (JSONException ex){
            String error = resJson.optString("non_field_errors");
            hMap.put(LoginErrors.GENERAL,error);
            return new LoginData(false, "none",hMap);
        }
    }

    public ArrayList<CoffeeAvailableData> getCofeeItemsList(JSONArray array) throws JSONException {
        ArrayList<CoffeeAvailableData> result = new ArrayList<>();

        for(int i=0; i<array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);
            result.add(new CoffeeAvailableData(jsonObj.getString("name"), jsonObj.getString("description"),
                    jsonObj.getString("id")));
        }
        return result;
    }

    public ProductData getProductData(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        return  new ProductData(json.optString("id"),
                json.optString("name"),
                json.optString("description"),
                json.optString("price"),
                json.optString("img"));
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

        OfferData offerData;
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

        PromotionData promData;
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
}
