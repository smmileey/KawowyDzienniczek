package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.MenuItem;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;

public class OffersDetailActivity extends AppCompatActivity {

    private String coffeeShopId;
    private String offerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offersactivity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            coffeeShopId =bundle.getString(FragmentsArgumentsConstants.COFFEE_SHOP_ID);
            offerId = bundle.getString(FragmentsArgumentsConstants.OFFER_ID);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FragmentsArgumentsConstants.COFFEE_SHOP_ID, coffeeShopId);
            arguments.putString(FragmentsArgumentsConstants.OFFER_ID, offerId);
            OffersDetailFragment fragment = new OffersDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.offersactivity_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent backIntent = new Intent(this, OffersListActivity.class);
            backIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID, coffeeShopId);
            navigateUpTo(backIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
