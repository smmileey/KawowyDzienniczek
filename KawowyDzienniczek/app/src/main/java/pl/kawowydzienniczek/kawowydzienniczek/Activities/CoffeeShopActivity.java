package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralService;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;

public class CoffeeShopActivity extends AppCompatActivity {

    private CoffeePreviewTask mCoffeeTask = null;
    private String token;
    private String coffeeId;
    private String rawServerResponse;

    private HttpService service = new HttpService();
    private HttpService.CoffeePreviewData coffeePreviewData;
    private GeneralService genService = new GeneralService();
    private GeneralUtilMethods genUtils;

    private View mProgressBar;
    private View mCoffeeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_shop);

        mCoffeeView = findViewById(R.id.included_layout);
        mProgressBar = findViewById(R.id.progress_bar_coffee_shop);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN, null);
        Bundle bundle= getIntent().getExtras();
        if(bundle != null){
            coffeeId = bundle.getString(GeneralConstants.COFFEE_SHOP_ID);
        }

        genUtils = new GeneralUtilMethods(getApplicationContext());
        genUtils.showProgress(true,mCoffeeView,mProgressBar);
        mCoffeeTask = new CoffeePreviewTask();
        mCoffeeTask.execute((Void) null);
    }

    public void showOffers(View view){
        Intent offerIntent = new Intent(getApplicationContext(),OffersListActivity.class);
        offerIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID, coffeeId);
        startActivity(offerIntent);
    }

    public void showPromotions(View view){
        Intent offerIntent = new Intent(getApplicationContext(),PromotionListActivity.class);
        offerIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID, coffeeId);
        startActivity(offerIntent);
    }

    public class CoffeePreviewTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if(mCoffeeTask != null) {
                try {
                    rawServerResponse  = service.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE + UrlEndingsConstants.API_PLACES + coffeeId + "/", token);
                    coffeePreviewData = service.getCoffeePreviewData(rawServerResponse);
                    return true;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCoffeeTask = null;
            genUtils.showProgress(false, mCoffeeView, mProgressBar);

            if(success){
                try {
                    if(service.isRequestAuthorized(rawServerResponse)) {
                        ImageView logoView = (ImageView) mCoffeeView.findViewById(R.id.image_logo_img);
                        ImageView screenView = (ImageView) mCoffeeView.findViewById(R.id.image_screen_img);
                        Picasso.with(getApplicationContext()).load(coffeePreviewData.getImage_logo()).resize(150, 150).into(logoView);
                        Picasso.with(getApplicationContext()).load(coffeePreviewData.getImage_screen()).into(screenView);

                        TextView titleView = (TextView) mCoffeeView.findViewById(R.id.single_coffee_title);
                        titleView.setText(coffeePreviewData.getName());
                        TextView localView = (TextView) mCoffeeView.findViewById(R.id.single_coffee_localization);
                        localView.setText(genService.getFormattedLocalization(coffeePreviewData.getLocalization()));
                    }else {
                        genUtils.ResetToken();
                        Intent backToLogin = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(backToLogin);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(),"Coś poszło nie tak:P",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCoffeeTask = null;
            genUtils.showProgress(false,mCoffeeView,mProgressBar);
        }
    }
}

