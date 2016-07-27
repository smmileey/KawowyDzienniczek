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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.UrlEndingsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Data.PromotionData;
import pl.kawowydzienniczek.kawowydzienniczek.Data.User;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;
import pl.kawowydzienniczek.kawowydzienniczek.Services.KawowyDzienniczekService;
import pl.kawowydzienniczek.kawowydzienniczek.Utilities.PromotionForUserAdapter;

public class PromotionListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private KawowyDzienniczekService kawowyDzienniczekService = new KawowyDzienniczekService();

    private int mActivatedPosition = ListView.INVALID_POSITION;
    private List<PromotionData> adapterItems;
    private PromotionDataTask mPromotionDataTask = null;
    private Activity activity;

    private String token;
    private User user;
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
        Gson gson = new Gson();
        user = gson.fromJson(prefs.getString(GeneralConstants.USER_PROFILE, null), User.class);

        activity = this.getActivity();
        coffeeShopId = ((PromotionListActivity)this.getActivity()).getCoffeeShopId();

        promotionCategory = getArguments() == null?
                GeneralConstants.USER_PROMOTION_PROGRESS_AVAILABLE : getArguments().getString(FragmentsArgumentsConstants.PROMOTION_CATEGORY);

        adapterItems = new ArrayList<>();
        PromotionForUserAdapter promAdapter = new PromotionForUserAdapter(activity.getApplicationContext(),
                adapterItems,Integer.toString(user.getId()) ,coffeeShopId, promotionCategory);
        setListAdapter(promAdapter);
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
        mPromotionDataTask.execute((Void) null);
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

        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

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
                       case GeneralConstants.USER_PROMOTION_PROGRESS_AVAILABLE:
                           rawServerResponse = kawowyDzienniczekService.getRequest(GeneralConstants.KAWOWY_DZIENNICZEK_WITH_SCHEME
                                   + UrlEndingsConstants.API_PLACES + coffeeShopId + "/", token);
                           kawowyDzienniczekService.getAvailablePromotionDataReplaceExistingList(adapterItems, rawServerResponse);
                           return true;

                       case GeneralConstants.USER_PROMOTION_PROGRESS_ACTIVE:
                           Gson gson = new Gson();
                           HashMap<String,String> args = new HashMap<>();
                           args.put(GeneralConstants.USER_PROMOTIONS_ACTIVE_ARGUMENT_PLACE,coffeeShopId);

                           rawServerResponse = kawowyDzienniczekService.getRequestWithParameters(GeneralConstants.KAWOWY_DZIENNICZEK_WITH_SCHEME
                                   + UrlEndingsConstants.API_USER_PROMOTIONS, token,args);
                           SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences(getString(R.string.app_name),
                                   Context.MODE_PRIVATE);

                           if(prefs.getBoolean(GeneralConstants.IS_ACTIVE_LIST_MODIFIED + coffeeShopId + user.getId(),true)) {
                               List<String> statusesWanted = new ArrayList<>();
                               statusesWanted.add(GeneralConstants.USER_PROMOTION_PROGRESS_ACTIVE);
                               statusesWanted.add(GeneralConstants.USER_PROMOTION_PROGRESS_AVAILABLE);

                               kawowyDzienniczekService.getPromotionDataByStatusReplaceExistingList(adapterItems, rawServerResponse,
                                       statusesWanted);
                               SharedPreferences.Editor editor = prefs.edit();
                               editor.putString(GeneralConstants.USER_PROMOTIONS_ACTIVE + coffeeShopId + user.getId() ,
                                       gson.toJson(adapterItems));
                               editor.putBoolean(GeneralConstants.IS_ACTIVE_LIST_MODIFIED + coffeeShopId + user.getId(), false);
                               editor.apply();
                           }else {
                               Type promListType = new TypeToken<ArrayList<PromotionData>>(){}.getType();
                               List<PromotionData> dataToPass = gson.fromJson(prefs.
                                       getString(GeneralConstants.USER_PROMOTIONS_ACTIVE + coffeeShopId + user.getId(), null), promListType);
                               kawowyDzienniczekService.copyArrayListByValue(dataToPass,adapterItems);
                           }
                           return true;

                       case GeneralConstants.USER_PROMOTION_PROGRESS_HISTORY:
                           HashMap<String,String> nargs = new HashMap<>();
                           nargs.put(GeneralConstants.USER_PROMOTIONS_ACTIVE_ARGUMENT_PLACE,coffeeShopId);

                           List<String> statusedWanted = new ArrayList<>();
                           statusedWanted.add(GeneralConstants.USER_PROMOTION_PROGRESS_HISTORY);
                           rawServerResponse = kawowyDzienniczekService.getRequestWithParameters(GeneralConstants.KAWOWY_DZIENNICZEK_WITH_SCHEME
                                   + UrlEndingsConstants.API_USER_PROMOTIONS, token, nargs);
                           kawowyDzienniczekService.getPromotionDataByStatusReplaceExistingList(adapterItems, rawServerResponse, statusedWanted);
                           return true;
                       default:
                           return false;
                   }
               } catch (IOException | JSONException | ParseException |IllegalStateException e) {
                   e.printStackTrace();
                   return false;
               }
           }else
                return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPromotionDataTask =null;

            if(success){
                try {
                    if(kawowyDzienniczekService.isRequestAuthorized(rawServerResponse)) {
                        ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                        ((PromotionListActivity)activity).getCurrentTabButton().setPressed(true);
                    }else {
                        GeneralUtilMethods kawowyDzienniczekService = new GeneralUtilMethods(activity.getApplicationContext());
                        kawowyDzienniczekService.ResetToken();
                        Intent backToLogin = new Intent(activity.getApplicationContext(),LoginActivity.class);
                        startActivity(backToLogin);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(activity.getApplicationContext(), "Take it easy bro!/or Server error hehe", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mPromotionDataTask =null;
        }
    }
}
