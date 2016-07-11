package pl.kawowydzienniczek.kawowydzienniczek.Services;


import org.json.JSONException;
import org.json.JSONObject;

public class GeneralService {

    public String getFormattedLocalization(String response){

        try {
            JSONObject json = new JSONObject(response);
            return json.getString("city")+", "+json.getString("road")+" "+json.getString("road_number");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
