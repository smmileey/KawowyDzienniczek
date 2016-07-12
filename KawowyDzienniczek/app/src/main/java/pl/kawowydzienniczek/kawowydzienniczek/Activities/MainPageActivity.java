package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;

public class MainPageActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        TextView tvUsername = (TextView) findViewById(R.id.username);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        token = prefs.getString(GeneralConstants.TOKEN,null);
        Gson gson = new Gson();

        HttpService.UserData userData = gson.fromJson(prefs.getString(GeneralConstants.USER_PROFILE, ""), HttpService.UserData.class);
        if(prefs.getBoolean(GeneralConstants.AUTHENTICATED,false)) {
            tvUsername.setText(userData.getUser().getUsername());
        }
    }

    public void onToolbarMenuClick(View v) {
        switch (v.getId()){
            case R.id.current_coffee:
                Toast.makeText(getApplicationContext(), "Aktualna Kawiarnia", Toast.LENGTH_SHORT).show();
                break;
            case R.id.my_coffees:
                Toast.makeText(getApplicationContext(),"Moje Kawiarnie",Toast.LENGTH_SHORT).show();
                break;
            case R.id.coffees_preview:
                Intent previewCoffeesIntent = new Intent(getApplicationContext(),PreviewCoffeeShopsActivity.class);
                startActivity(previewCoffeesIntent);
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
