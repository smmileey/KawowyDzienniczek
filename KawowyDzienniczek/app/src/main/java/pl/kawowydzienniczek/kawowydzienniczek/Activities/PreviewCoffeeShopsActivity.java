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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Globals.CoffeeAvailableData;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.CoffeeAvailableAdapter;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.JsonConverter;

public class PreviewCoffeeShopsActivity extends AppCompatActivity {

    private RetrieveGetTask mGetTask;
    private ListView mCofeeListView;
    private View mProgressBarView;

    private String token;
    private GeneralUtilMethods genUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_coffee_shops);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN,null);
        mCofeeListView = (ListView)findViewById(R.id.lw_cofee_shops);
        mProgressBarView = findViewById(R.id.progress_bar);

        mCofeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CoffeeAvailableData lwData = (CoffeeAvailableData)mCofeeListView.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), CoffeeShopActivity.class);
                intent.putExtra(GeneralConstants.COFFEE_SHOP_ID,lwData.getId());
                startActivity(intent);
            }
        });

        genUtils = new GeneralUtilMethods(getApplicationContext());
        genUtils.showProgress(true,mCofeeListView,mProgressBarView);
        mGetTask = new RetrieveGetTask();
        mGetTask.execute((Void) null);
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
            genUtils.showProgress(false, mCofeeListView, mProgressBarView);

            if(success) {
                try {
                    if (service.isRequestAuthorized(rawResponseData)) {
                        JsonConverter conv = new JsonConverter();
                        ArrayList<CoffeeAvailableData> values = conv.getCofeeItemsList(new JSONObject(rawResponseData).getJSONArray("results"));
                        CoffeeAvailableAdapter cAdapater = new CoffeeAvailableAdapter(getApplicationContext(), values);
                        mCofeeListView.setAdapter(cAdapater);

                    }else { //invalid data (np. token wygasł, itd.)
                        genUtils.ResetToken();
                        Intent backToLogin = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(backToLogin);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(PreviewCoffeeShopsActivity.this, "Pobranie listy kawiarni nie powiodło się (błąd serwera)", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetTask = null;
            genUtils.showProgress(false, mCofeeListView, mProgressBarView);
        }
    }
}
