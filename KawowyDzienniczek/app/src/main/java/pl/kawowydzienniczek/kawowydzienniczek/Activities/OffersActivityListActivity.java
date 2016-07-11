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

public class OffersActivityListActivity extends AppCompatActivity
        implements OffersActivityListFragment.Callbacks {

    private boolean mTwoPane;
    private String coffeeID;

    public String getCoffeeID() {
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
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((OffersActivityListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.offersactivity_list))
                    .setActivateOnItemClick(true);
        }
    }

    /**
     * Callback method from {@link OffersActivityListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FragmentsArgumentsConstants.PRODUCT_ID, id);
            OffersActivityDetailFragment fragment = new OffersActivityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.offersactivity_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OffersActivityDetailActivity.class);
            detailIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID,coffeeID);
            detailIntent.putExtra(FragmentsArgumentsConstants.PRODUCT_ID,id);
            startActivity(detailIntent);
        }
    }
}
