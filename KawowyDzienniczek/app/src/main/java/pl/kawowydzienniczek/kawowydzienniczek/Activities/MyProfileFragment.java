package pl.kawowydzienniczek.kawowydzienniczek.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Data.UserData;
import pl.kawowydzienniczek.kawowydzienniczek.R;

public class MyProfileFragment extends Fragment {

    private Activity activity;
    private TextView tvUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        //TODO: jak nie jest authenticated, to log out
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        tvUsername = (TextView) fragmentView.findViewById(R.id.username_login);

        Gson gson = new Gson();
        SharedPreferences prefs = activity.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        UserData userData = gson.
                fromJson(prefs.getString(GeneralConstants.USER_PROFILE, ""), UserData.class);
        if(prefs.getBoolean(GeneralConstants.AUTHENTICATED,false)) {
            tvUsername.setText(userData.getUser().getUsername());
        }

        return fragmentView;
    }

    public MyProfileFragment() {
        // Required empty public constructor
    }
}
