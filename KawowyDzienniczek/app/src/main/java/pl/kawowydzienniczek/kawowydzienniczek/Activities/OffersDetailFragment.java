package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Services.KawowyDzienniczekService;

public class OffersDetailFragment extends Fragment {

    private KawowyDzienniczekService kawowyDzienniczekService = new KawowyDzienniczekService();
    private GeneralUtilMethods generalUtilMethods;

    private ProductDataTask mProductDataTask = null;
    private KawowyDzienniczekService.ProductData productData;
    private String productId;
    private String token;
    private String rawServerResponse;
    private Activity activity;
    private View rootView;
    private View mainLayout;
    private View progressView;

    public OffersDetailFragment() {
        //mandatory
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(FragmentsArgumentsConstants.OFFER_ID)) {

            productId = getArguments().getString(FragmentsArgumentsConstants.OFFER_ID);
            SharedPreferences prefs = this.getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            token = prefs.getString(GeneralConstants.TOKEN,null);

            activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Wczytywanie...");
            }
            generalUtilMethods = new GeneralUtilMethods(activity.getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_offersactivity_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(this.getActivity() instanceof OffersListActivity){
            mainLayout = this.getActivity().findViewById(R.id.frameLayout);
            progressView = this.getActivity().findViewById(R.id.progress_bar_offers_fragment);
        }else {
            mainLayout = this.getActivity().findViewById(R.id.offersactivity_detail_container);
            progressView = this.getActivity().findViewById(R.id.progress_bar_offers_fragment_details);
        }

        generalUtilMethods.showProgress(true,mainLayout,progressView);
        mProductDataTask = new ProductDataTask();
        mProductDataTask.execute((Void) null);
    }

    public class ProductDataTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(mProductDataTask != null) {
                    rawServerResponse = kawowyDzienniczekService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_WITH_SCHEME + UrlEndingsConstants.API_PRODUCTS + productId + "/", token);
                    productData = kawowyDzienniczekService.getProductData(rawServerResponse);
                    return true;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            generalUtilMethods.showProgress(false,mainLayout,progressView);
            mProductDataTask = null;

            if(success){
                try {
                    if(kawowyDzienniczekService.isRequestAuthorized(rawServerResponse)) {
                        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                        if (appBarLayout != null) {
                            appBarLayout.setTitle(productData.getName());
                        }

                        ((TextView) rootView.findViewById(R.id.product_name)).setText(productData.getName());
                        ((TextView) rootView.findViewById(R.id.product_description)).setText(productData.getDescription());
                        ImageView img = (ImageView) rootView.findViewById(R.id.product_image);
                        Picasso.with(activity.getApplicationContext()).load(productData.getImg()).into(img);
                        ((TextView) rootView.findViewById(R.id.product_price)).setText(productData.getPrice());
                    }else {
                        GeneralUtilMethods genUtils = new GeneralUtilMethods(activity.getApplicationContext());
                        genUtils.ResetToken();
                        Intent backToLogin = new Intent(activity.getApplicationContext(),LoginActivity.class);
                        startActivity(backToLogin);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(activity.getApplicationContext(),"Brak informacji o podanym produkcie!",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProductDataTask = null;
            generalUtilMethods.showProgress(false,mainLayout,progressView);
        }
    }
}
