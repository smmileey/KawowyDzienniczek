package pl.kawowydzienniczek.kawowydzienniczek.Utilities;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.kawowydzienniczek.kawowydzienniczek.Globals.CoffeeAvailableData;

public class JsonConverter {

    public ArrayList<CoffeeAvailableData> getCofeeItemsList(JSONArray array) throws JSONException {

        ArrayList<CoffeeAvailableData> result = new ArrayList<>();

        for(int i=0; i<array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);
            result.add(new CoffeeAvailableData(jsonObj.getString("name"), jsonObj.getString("description"),
                    jsonObj.getString("id")));
        }
        return result;
    }


}
