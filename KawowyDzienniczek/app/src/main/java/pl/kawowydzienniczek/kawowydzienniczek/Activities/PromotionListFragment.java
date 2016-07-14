package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;

public class PromotionListFragment extends ListFragment {


    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private HttpService httpService = new HttpService();

    private int mActivatedPosition = ListView.INVALID_POSITION;
    private List<HttpService.PromotionData> adapterItems;
    private PromotionDataTask mPromotionDataTask = null;
    private Activity activity;

    private String token;
    private String rawServerResponse;
    private String coffeeShopId;
    private String promotionCategory;

    public interface Callbacks {
        void onItemSelected(String id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {//DUMMY IMPLEMENTATION
         }
    };

    public PromotionListFragment() { //mandatory
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = this.getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN, null);

        activity = this.getActivity();
        coffeeShopId = ((PromotionListActivity)this.getActivity()).getCoffeeShopId();

        promotionCategory = getArguments() == null?
                GeneralConstants.PROMOTION_AVAILABLE: getArguments().getString(FragmentsArgumentsConstants.PROMOTION_CATEGORY);

        adapterItems = new ArrayList<>();
        setListAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                adapterItems));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPromotionDataTask = new PromotionDataTask();
        mPromotionDataTask.execute((Void)null);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(adapterItems.get(position).getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public class PromotionDataTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
           if(mPromotionDataTask != null){
               try {
                   switch (promotionCategory){
                       case GeneralConstants.PROMOTION_AVAILABLE:
                           rawServerResponse = httpService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE
                                   + UrlEndingsConstants.API_PROMOTIONS_SET, token);
                           httpService.getAllPromotionsDataReplaceExisting(adapterItems, rawServerResponse, Integer.parseInt(coffeeShopId));
                           return true;
                       case GeneralConstants.PROMOTION_ACTIVE:
                           rawServerResponse = httpService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE
                                   + UrlEndingsConstants.API_USER_PROMOTIONS, token);
                           httpService.getPersonalPromotionDataReplaceExisting(adapterItems, rawServerResponse, GeneralConstants.PROMOTION_ACTIVE);
                           return true;
                       case GeneralConstants.PROMOTION_HISTORY: //TODO: make filtering by active/history properly
                           rawServerResponse = httpService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE
                               + UrlEndingsConstants.API_USER_PROMOTIONS, token);
                           httpService.getPersonalPromotionDataReplaceExisting(adapterItems, rawServerResponse, GeneralConstants.PROMOTION_HISTORY);
                           return true;
                       default:
                           return false;
                   }
               } catch (IOException | JSONException | ParseException e) {
                   e.printStackTrace();
               }
           }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPromotionDataTask =null;

            if(success){
                try {
                    if(httpService.isRequestAuthorized(rawServerResponse)) {
                        ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                        ((PromotionListActivity)activity).getCurrentTabButton().setPressed(true);
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
                Toast.makeText(activity.getApplicationContext(), "Brak promocji do pobrania!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mPromotionDataTask =null;
        }
    }
}
