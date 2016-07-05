package pl.kawowydzienniczek.kawowydzienniczek;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainPageActivity extends AppCompatActivity {

    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        tvUsername = (TextView)findViewById(R.id.username);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if(prefs.getBoolean("authenticated",false))
            tvUsername.setText(prefs.getString("username", "NO-USERNAME-PROVIDED"));
    }
}
