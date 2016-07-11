package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.CoffeeAvailableAdapter;
import pl.kawowydzienniczek.kawowydzienniczek.Globals.CoffeeAvailableData;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.JsonConverter;

public class MainPageActivity extends AppCompatActivity {

    private RetrieveGetTask mGetTask;
    private ListView mCofeeListView;
    private View mMainView;
    private View mProgressBarView;

    private String token;
    private GeneralUtilMethods genUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        TextView tvUsername = (TextView) findViewById(R.id.username);
        mCofeeListView = (ListView)findViewById(R.id.lw_cofee_shops);
        mMainView = findViewById(R.id.main_page);
        mProgressBarView = findViewById(R.id.progress_bar);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN,null);
        Gson gson = new Gson();
        HttpService.UserData userData = gson.fromJson(prefs.getString(GeneralConstants.USER_PROFILE, ""), HttpService.UserData.class);

        if(prefs.getBoolean(GeneralConstants.AUTHENTICATED,false)) {
            tvUsername.setText(userData.getUser().getUsername());
        }

        mCofeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CoffeeAvailableData lwData = (CoffeeAvailableData)mCofeeListView.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), CoffeeShopActivity.class);
                intent.putExtra(GeneralConstants.ID,lwData.getId());
                startActivity(intent);
            }
        });

        genUtils = new GeneralUtilMethods(getApplicationContext());
    }

    public void getCofeeShops(View view){
        genUtils.showProgress(true,mMainView,mProgressBarView);
        mGetTask = new RetrieveGetTask();
        mGetTask.execute((Void)null);
    }

    public void LogOut(View view){
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE).edit();
        editor.putBoolean(GeneralConstants.AUTHENTICATED, false);
        editor.putString(GeneralConstants.TOKEN, null);
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
                    rawResponseData = service.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE + UrlEndingsConstants.API_PLACES, token);
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
            genUtils.showProgress(false,mMainView,mProgressBarView);

            if(success) {
                try {
                    if (service.isRequestAuthorized(rawResponseData)) {
                        JsonConverter conv = new JsonConverter();
                        ArrayList<CoffeeAvailableData> values = conv.getCofeeItemsList(new JSONObject(rawResponseData).getJSONArray("results"));
                        CoffeeAvailableAdapter cAdapater = new CoffeeAvailableAdapter(getApplicationContext(), values);
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
        protected void onCancelled() {
            mGetTask = null;
            genUtils.showProgress(false,mMainView,mProgressBarView);
        }

        private void ResetToken(){
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
            editor.putString(GeneralConstants.TOKEN, null);
            editor.putBoolean(GeneralConstants.AUTHENTICATED, false);
            editor.apply();
        }
    }
}
