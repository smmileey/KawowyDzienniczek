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

public class PromotionListActivity extends AppCompatActivity
        implements PromotionListFragment.Callbacks {

    private boolean mTwoPane;
    private String coffeeShopId;

    public String getCoffeeShopId() {
        return coffeeShopId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            coffeeShopId = bundle.getString(FragmentsArgumentsConstants.COFFEE_SHOP_ID);
        setContentView(R.layout.activity_promotion_app_bar);

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

        if (findViewById(R.id.promotion_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((PromotionListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.promotion_list))
                    .setActivateOnItemClick(true);
        }
    }

    /**
     * Callback method from {@link PromotionListFragment.Callbacks}
     * indicating that the item with the given COFFEE_SHOP_ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(FragmentsArgumentsConstants.PROMOTION_ID, id);
            PromotionDetailFragment fragment = new PromotionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.promotion_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, PromotionDetailActivity.class);
            detailIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID, coffeeShopId);
            detailIntent.putExtra(FragmentsArgumentsConstants.PROMOTION_ID,id);
            startActivity(detailIntent);
        }
    }
}
