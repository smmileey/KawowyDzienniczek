package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import pl.kawowydzienniczek.kawowydzienniczek.Services.KawowyDzienniczekService;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.CoffeeAvailableAdapter;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.JsonConverter;

public class PreviewCoffeeShopsFragment extends Fragment {

    private RetrieveGetTask mGetTask;
    private ListView mCofeeListView;
    private View mProgressBarView;

    private String token;
    private GeneralUtilMethods genUtils;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        SharedPreferences prefs = activity.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN,null);

        genUtils = new GeneralUtilMethods(activity.getApplicationContext());
    }

    public PreviewCoffeeShopsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_preview_coffee_shops,container,false);
        mCofeeListView = (ListView)fragmentView.findViewById(R.id.lw_cofee_shops);
        mProgressBarView = fragmentView.findViewById(R.id.progress_bar);

        mCofeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CoffeeAvailableData lwData = (CoffeeAvailableData)mCofeeListView.getAdapter().getItem(position);
                Intent intent = new Intent(activity.getApplicationContext(), CoffeeShopActivity.class);
                intent.putExtra(GeneralConstants.COFFEE_SHOP_ID,lwData.getId());
                startActivity(intent);
            }
        });

        genUtils.showProgress(true,mCofeeListView,mProgressBarView);
        mGetTask = new RetrieveGetTask();
        mGetTask.execute((Void) null);
        return fragmentView;
    }

    public class RetrieveGetTask extends AsyncTask<Void, Void, Boolean> {

        private KawowyDzienniczekService service = new KawowyDzienniczekService();
        private String rawResponseData;

        @Override
        protected Boolean doInBackground(Void... params) {

            if (token != null) {
                try {
                    rawResponseData = service.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_WITH_SCHEME + UrlEndingsConstants.API_PLACES, token);
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
                        CoffeeAvailableAdapter cAdapater = new CoffeeAvailableAdapter(activity.getApplicationContext(), values);
                        mCofeeListView.setAdapter(cAdapater);

                    }else { //invalid data (np. token wygasł, itd.)
                        genUtils.ResetToken();
                        Intent backToLogin = new Intent(activity.getApplicationContext(),LoginActivity.class);
                        startActivity(backToLogin);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(activity.getApplicationContext(), "Pobranie listy kawiarni nie powiodło się (błąd serwera)", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetTask = null;
            genUtils.showProgress(false, mCofeeListView, mProgressBarView);
        }
    }
}
