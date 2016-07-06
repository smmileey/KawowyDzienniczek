package pl.kawowydzienniczek.kawowydzienniczek.Utilities;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonConverter {

    public ArrayList<CoffeeItem> getCofeeItemsList(JSONArray array) throws JSONException {

        ArrayList<CoffeeItem> result = new ArrayList<>();

        for(int i=0; i<array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);
            result.add(new CoffeeItem(jsonObj.getString("name"), jsonObj.getString("description")));
        }
        return result;
    }


}
