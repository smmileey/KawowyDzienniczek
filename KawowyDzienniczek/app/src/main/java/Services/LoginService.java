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

public class LoginService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String postRequest(String url, String jsonToPass) throws IOException {
        RequestBody body = RequestBody.create(JSON, jsonToPass);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
        //return "{'email':'"+email+"','password':'"+password+"'}";
    }

    public ResponseData getToken(String response) throws JSONException {
        JSONObject resJson = new JSONObject(response);
        HashMap<String,String> hMap = new HashMap<>();
        try {
            String token = resJson.getString("token");
            hMap.put("errorsNum","0");
            return new ResponseData(true, token, hMap);
        }
        catch (JSONException ex){
            //TODO: logic to add errors (w8 for Rafał ujednolicenie)
            return new ResponseData(false, "none",hMap);
        }
    }

    public class ResponseData{

        private String token;
        private boolean isValid;
        private HashMap<String, String> errors; //TODO: uzupełnić zwrócone błędy, jak Rafał ujednolici

        public ResponseData(boolean valid, String tkn, HashMap<String,String> err){
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

        public HashMap<String,String> getErrors() {
            return errors;
        }
    }
}
