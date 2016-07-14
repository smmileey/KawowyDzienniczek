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
import java.text.ParseException;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;


public class PromotionDetailFragment extends Fragment {

    private HttpService httpService = new HttpService();
    private GeneralUtilMethods generalUtilMethods;

    private SinglePromotionDataTask mSinglePromotionDataTask = null;
    private HttpService.PromotionData promotionData;
    private String promotionId;
    private String token;
    private String rawServerResponse;
    private Activity activity;
    private View rootView;
    private View mainLayout;
    private View progressView;

    public PromotionDetailFragment() {//mandatory
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(FragmentsArgumentsConstants.PROMOTION_ID)) {

            promotionId = getArguments().getString(FragmentsArgumentsConstants.PROMOTION_ID);
            SharedPreferences prefs = this.getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            token = prefs.getString(GeneralConstants.TOKEN,null);

            activity = this.getActivity();

            Activity activity = this.getActivity();
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
        rootView = inflater.inflate(R.layout.fragment_promotion_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(this.getActivity() instanceof PromotionListActivity){
            mainLayout = this.getActivity().findViewById(R.id.frameLayout);
            progressView = this.getActivity().findViewById(R.id.progress_bar_promotions_fragment);
        }else {
            mainLayout = this.getActivity().findViewById(R.id.promotion_detail_container);
            progressView = this.getActivity().findViewById(R.id.progress_bar_promotions_fragment_details);
        }

        generalUtilMethods.showProgress(true,mainLayout,progressView);
        mSinglePromotionDataTask = new SinglePromotionDataTask();
        mSinglePromotionDataTask.execute((Void) null);
    }

    public class SinglePromotionDataTask extends AsyncTask<Void,Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            if(mSinglePromotionDataTask != null){
                try {
                    rawServerResponse = httpService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE+
                            UrlEndingsConstants.API_PROMOTIONS_SINGLE + promotionId + "/",token);
                    promotionData = httpService.getSinglePromotionData(rawServerResponse);
                    return true;
                } catch (IOException | JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSinglePromotionDataTask = null;
            generalUtilMethods.showProgress(false,mainLayout,progressView);

            if(success){
                try {
                    if(httpService.isRequestAuthorized(rawServerResponse)) {
                        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                        if (appBarLayout != null) {
                            appBarLayout.setTitle(promotionData.getName());
                        }

                        ((TextView) rootView.findViewById(R.id.promotion_name)).setText(promotionData.getName());
                        ((TextView) rootView.findViewById(R.id.promotion_description)).setText(promotionData.getDescription());
                        ImageView img = (ImageView) rootView.findViewById(R.id.icon_image);
                        Picasso.with(activity.getApplicationContext()).load(promotionData.getImage()).resize(155, 155)
                                .into(img);
                        ((TextView) rootView.findViewById(R.id.promotion_code)).setText(promotionData.getCode());
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
            }else {
                Toast.makeText(activity.getApplicationContext(), "Brak informacji o podanej promocji w bazie!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSinglePromotionDataTask = null;
            generalUtilMethods.showProgress(false,mainLayout,progressView);
        }
    }
}
