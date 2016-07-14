package pl.kawowydzienniczek.kawowydzienniczek.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.FragmentsArgumentsConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;

public class PromotionListActivity extends AppCompatActivity
        implements PromotionListFragment.Callbacks {

    private boolean mTwoPane;
    private String coffeeShopId;
    private String currentPromotionTab;
    private Button currentTabButton;

    public String getCoffeeShopId() {
        return coffeeShopId;
    }

    public Button getCurrentTabButton() {
        return currentTabButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            coffeeShopId = bundle.getString(FragmentsArgumentsConstants.COFFEE_SHOP_ID);
        }

        setContentView(R.layout.activity_promotion_app_bar);

        if(bundle != null) {
            currentPromotionTab = bundle.getString(FragmentsArgumentsConstants.PROMOTION_CATEGORY);
            if (currentPromotionTab == null) {
                currentPromotionTab = GeneralConstants.PROMOTION_AVAILABLE;
                currentTabButton = (Button) findViewById(R.id.btn_promotions_available);
            } else {
                switch (currentPromotionTab) {
                    case GeneralConstants.PROMOTION_ACTIVE:
                        currentTabButton = (Button) findViewById(R.id.btn_promotions_active);
                        break;
                    case GeneralConstants.PROMOTION_HISTORY:
                        currentTabButton = (Button) findViewById(R.id.btn_promotions_history);
                        break;
                    case GeneralConstants.PROMOTION_AVAILABLE:
                        currentTabButton = (Button)findViewById(R.id.btn_promotions_available);
                }
            }
            currentTabButton.setPressed(true);
        }

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

            mTwoPane = true;

            ((PromotionListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.promotion_list))
                    .setActivateOnItemClick(true);
        }
        ReplaceFragmentWithProductNames(currentPromotionTab);
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(FragmentsArgumentsConstants.PROMOTION_ID, id);
            arguments.putString(FragmentsArgumentsConstants.PROMOTION_CURRENT, currentPromotionTab);
            PromotionDetailFragment fragment = new PromotionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.promotion_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, PromotionDetailActivity.class);
            detailIntent.putExtra(FragmentsArgumentsConstants.COFFEE_SHOP_ID, coffeeShopId);
            detailIntent.putExtra(FragmentsArgumentsConstants.PROMOTION_ID,id);
            detailIntent.putExtra(FragmentsArgumentsConstants.PROMOTION_CURRENT, currentPromotionTab);
            startActivity(detailIntent);
        }
    }

    public void OnCategoryClick(View view){
        switch (view.getId()){
            case R.id.btn_promotions_available:
                currentTabButton.setPressed(false);
                currentTabButton = (Button)findViewById(R.id.btn_promotions_available);
                ReplaceFragmentWithProductNames(GeneralConstants.PROMOTION_AVAILABLE);
                break;
            case R.id.btn_promotions_active:
                currentTabButton.setPressed(false);
                currentTabButton = (Button)findViewById(R.id.btn_promotions_active);
                ReplaceFragmentWithProductNames(GeneralConstants.PROMOTION_ACTIVE);
                break;
            case R.id.btn_promotions_history:
                currentTabButton.setPressed(false);
                currentTabButton = (Button)findViewById(R.id.btn_promotions_history);
                ReplaceFragmentWithProductNames(GeneralConstants.PROMOTION_HISTORY);
                break;
        }
    }

    private void ReplaceFragmentWithProductNames(String categoryKind){
        currentPromotionTab = categoryKind;
        PromotionListFragment listFragment = new PromotionListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(FragmentsArgumentsConstants.PROMOTION_CATEGORY, categoryKind);
        listFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.promtion_list_container,listFragment)
                .commit();
    }
}
