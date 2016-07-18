package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;

public class MainPageActivity extends AppCompatActivity {

    private String token;
    private String currentCategory;
    private FragmentManager fragmentManager;
    private Map<String, Integer> fragmentTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN,null);
        currentCategory = FragmentsArgumentsConstants.MY_PROFILE;

        fragmentManager = getSupportFragmentManager();
        fragmentTags = new HashMap<>();
        UpdateFragmentTagsList();

        ReplaceFragmentRetainState(FragmentsArgumentsConstants.MY_PROFILE, new MyProfileFragment());
    }

    private void UpdateFragmentTagsList(){
        fragmentTags.put(FragmentsArgumentsConstants.MY_PROFILE, 0);
        fragmentTags.put(FragmentsArgumentsConstants.CURRENT_COFFEE_SHOP,1);
        fragmentTags.put(FragmentsArgumentsConstants.MY_COFFEE_SHOPS,2);
        fragmentTags.put(FragmentsArgumentsConstants.NEAREST_COFFEE_SHOPS,3);
        fragmentTags.put(FragmentsArgumentsConstants.SETTINGS, 4);
    }

    private void ReplaceFragmentRetainState(String newCategory, Fragment fragment){
        int animIn;
        int animOut;

        if(fragmentTags.get(newCategory)> fragmentTags.get(currentCategory)){
            animIn = R.anim.slide_from_right;
        }else {
            animIn = R.anim.slide_from_left;
        }

        if(fragmentManager.findFragmentByTag(newCategory)!= null){
            fragmentManager.beginTransaction()
                    .setCustomAnimations(animIn,animIn)
                    .show(fragmentManager.findFragmentByTag(newCategory))
                    .commit();
        }else {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(animIn,animIn)
                    .add(R.id.main_panel_container, fragment,
                            newCategory).commit();
        }

        if(fragmentManager.findFragmentByTag(currentCategory) != null){
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(currentCategory))
                    .commit();
        }

        currentCategory = newCategory;
    }

    public void onToolbarMenuClick(View v) {
        switch (v.getId()){
            case R.id.my_profile:
                if(!currentCategory.equals(FragmentsArgumentsConstants.MY_PROFILE)) {
                    ReplaceFragmentRetainState(FragmentsArgumentsConstants.MY_PROFILE, new MyProfileFragment());
                }
                break;

            case R.id.current_coffee:
                Toast.makeText(getApplicationContext(), "Aktualna Kawiarnia", Toast.LENGTH_SHORT).show();
                break;

            case R.id.my_coffees:
                Toast.makeText(getApplicationContext(),"Moje Kawiarnie",Toast.LENGTH_SHORT).show();
                break;

            case R.id.coffees_preview:
                if(!currentCategory.equals(FragmentsArgumentsConstants.NEAREST_COFFEE_SHOPS)) {
                    ReplaceFragmentRetainState(FragmentsArgumentsConstants.NEAREST_COFFEE_SHOPS, new PreviewCoffeeShopsFragment());
                }
                break;

            case R.id.settings:
                Toast.makeText(getApplicationContext(),"Ustawienia",Toast.LENGTH_SHORT).show();
                break;

            case R.id.log_out:
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
                editor.putBoolean(GeneralConstants.AUTHENTICATED, false);
                editor.putString(GeneralConstants.TOKEN, null);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
        }
    }
}
