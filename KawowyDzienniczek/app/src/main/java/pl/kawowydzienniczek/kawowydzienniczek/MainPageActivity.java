package pl.kawowydzienniczek.kawowydzienniczek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Services.HttpService;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.CoffeeAdapter;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.CoffeeItem;
import pl.kawowydzienniczek.kawowydzienniczek.Info.Generals;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.JsonConverter;
import pl.kawowydzienniczek.kawowydzienniczek.Info.URLS;

public class MainPageActivity extends AppCompatActivity {

    private RetrieveGetTask mGetTask;
    private ListView mCofeeListView;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        TextView tvUsername = (TextView) findViewById(R.id.username);
        mCofeeListView = (ListView)findViewById(R.id.lw_cofee_shops);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if(prefs.getBoolean(Generals.AUTHENTICATED,false)) {
            tvUsername.setText(prefs.getString(Generals.USERNAME, "NO-USERNAME-PROVIDED"));
        }

        token = prefs.getString(Generals.TOKEN,null);
    }

    public void getCofeeShops(View view){
       mGetTask = new RetrieveGetTask();
        mGetTask.execute((Void)null);
    }

    public void LogOut(View view){
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE).edit();
        editor.putBoolean(Generals.AUTHENTICATED, false);
        editor.putString(Generals.TOKEN, null);
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public class RetrieveGetTask extends AsyncTask<Void, Void, Boolean> {

        private HttpService service = new HttpService();
        private String rawResponseData;

        @Override
        protected Boolean doInBackground(Void... params) {

            if (token != null) {
                try {
                    rawResponseData = service.getRequest(Generals.KAWOWY_DZIENNICZEK_BASE + URLS.API_PLACES, token);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return  false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetTask = null;
            if(success) {
                try {
                    if (service.isRequestAuthorized(rawResponseData)) {
                        JsonConverter conv = new JsonConverter();
                        ArrayList<CoffeeItem> values = conv.getCofeeItemsList(new JSONObject(rawResponseData).getJSONArray("results"));
                        CoffeeAdapter cAdapater = new CoffeeAdapter(getApplicationContext(), values);
                        mCofeeListView.setAdapter(cAdapater);

                    }else { //invalid data (np. token wygasł, itd.)
                        ResetToken();
                        Intent backToLogin = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(backToLogin);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(MainPageActivity.this, "Pobranie listy kawiarni nie powiodło się (błąd serwera)", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled(){
            mGetTask = null;
        }

        private void ResetToken(){
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
            editor.putString(Generals.TOKEN, null);
            editor.putBoolean(Generals.AUTHENTICATED, false);
            editor.apply();
        }
    }
}
