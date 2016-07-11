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
import java.util.List;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;

public class PromotionListFragment extends ListFragment {


    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private HttpService httpService = new HttpService();
    private GeneralUtilMethods genUtils;

    private int mActivatedPosition = ListView.INVALID_POSITION;
    private List<HttpService.PromotionData> adapterItems;
    private PromotionDataTask mPromotionDataTask = null;
    private Activity activity;

    private String token;
    private String rawServerResponse;
    private View mainLayout;
    private View progressView;
    private String coffeeShopId;

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
        genUtils = new GeneralUtilMethods(activity.getApplicationContext());
        coffeeShopId = ((PromotionListActivity)this.getActivity()).getCoffeeShopId();
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

        mainLayout = this.getActivity().findViewById(R.id.frameLayout);
        progressView = this.getActivity().findViewById(R.id.progress_bar_promotions_fragment);

        genUtils.showProgress(true,mainLayout,progressView);
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

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
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
                   rawServerResponse = httpService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_BASE
                           + UrlEndingsConstants.API_PROMOTIONS_SET,token);
                    adapterItems = httpService.getAllPromotionsData(rawServerResponse, Integer.parseInt(coffeeShopId));
                   return true;
               } catch (IOException | JSONException | ParseException e) {
                   e.printStackTrace();
               }
           }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPromotionDataTask =null;
            genUtils.showProgress(false, mainLayout, progressView);

            if(success){
                try {
                    if(httpService.isRequestAuthorized(rawServerResponse)) {
                        setListAdapter(new ArrayAdapter<>(
                                getActivity(),
                                android.R.layout.simple_list_item_activated_1,
                                android.R.id.text1,
                                adapterItems));
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
            genUtils.showProgress(false, mainLayout, progressView);
        }
    }
}
