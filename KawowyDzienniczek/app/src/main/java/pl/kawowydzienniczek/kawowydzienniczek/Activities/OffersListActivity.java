package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;

public class OffersListActivity extends AppCompatActivity
        implements OffersListFragment.Callbacks {

    private boolean mTwoPane;
    private String coffeeID;

    public String getCoffeeShopId() {
        return coffeeID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            coffeeID = bundle.getString(FragmentsArgumentsConstants.COFFEE_SHOP_ID);
        setContentView(R.layout.activity_offersactivity_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.offersactivity_detail_container) != null) {
            mTwoPane = true;

            ((OffersListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.offersactivity_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(FragmentsArgumentsConstants.OFFER_ID, id);
            OffersDetailFragment fragment = new OffersDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.offersactivity_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, OffersDetailActivity.class);
            detailIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID,coffeeID);
            detailIntent.putExtra(FragmentsArgumentsConstants.OFFER_ID,id);
            startActivity(detailIntent);
        }
    }
}
