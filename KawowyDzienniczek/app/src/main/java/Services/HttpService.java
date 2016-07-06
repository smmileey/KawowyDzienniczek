package Services;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kawowydzienniczek.kawowydzienniczek.Info.LoginErrors;

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

    public ResponseData getToken(String response) throws JSONException {
        JSONObject resJson = new JSONObject(response);
        HashMap<LoginErrors,String> hMap = new HashMap<>();
        try {
            String token = resJson.getString("token");
            return new ResponseData(true, token, hMap);
        }
        catch (JSONException ex){
            String error = resJson.optString("non_field_errors");
            hMap.put(LoginErrors.GENERAL,error);
            return new ResponseData(false, "none",hMap);
        }
    }

    public Boolean isRequestAuthorized(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        return obj.optString("detail").isEmpty();
    }

    public class ResponseData{

        private String token;
        private boolean isValid;
        private HashMap<LoginErrors, String> errors;

        public ResponseData(boolean valid, String tkn, HashMap<LoginErrors,String> err){
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
}
