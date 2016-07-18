package pl.kawowydzienniczek.kawowydzienniczek.Services;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    public <T> Boolean copyArrayListByValue(List<T> source, List<T> destination){
        if(destination == null)
            return false;

        destination.clear();
        for (T item:source) {
                destination.add(item);
        }
        return true;
    }
}
