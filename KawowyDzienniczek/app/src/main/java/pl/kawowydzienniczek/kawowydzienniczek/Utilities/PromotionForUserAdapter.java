package pl.kawowydzienniczek.kawowydzienniczek.Utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.KawowyDzienniczekService;

public class PromotionForUserAdapter extends ArrayAdapter<KawowyDzienniczekService.PromotionData> {

    private final int ACTIVE_FOR_USER;
    private final int NOT_ACTIVE_FOR_USER;

    private final Context context;
    private final String promCategory;
    private final List<KawowyDzienniczekService.PromotionData> itemsArrayList;
    private final List<KawowyDzienniczekService.PromotionData> promData;

    public PromotionForUserAdapter(Context context, List<KawowyDzienniczekService.PromotionData> itemsArrayList, String userId, String coffeeShopId,
                                   String promotionCategory){
        super(context, R.layout.promotion_list_item_with_state_status, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        promCategory = promotionCategory;

        ACTIVE_FOR_USER =  context.getResources().getIdentifier("presence_online","drawable","android");
        NOT_ACTIVE_FOR_USER = context.getResources().getIdentifier("transparent","color","android");

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type promListType = new TypeToken<ArrayList<KawowyDzienniczekService.PromotionData>>(){}.getType();
        promData = gson.fromJson(prefs.getString(GeneralConstants.USER_PROMOTIONS_ACTIVE + coffeeShopId +
                userId, null), promListType);
    }

    static class ViewHolder{
        TextView mainText;
        ImageView activeForUserIndicatorImage;
        List<KawowyDzienniczekService.PromotionData> promData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(promCategory.equals(GeneralConstants.USER_PROMOTION_PROGRESS_AVAILABLE)) {
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.promotion_list_item_with_state_status, parent, false);
                viewHolder = new ViewHolder();

                viewHolder.mainText = (TextView) convertView.findViewById(R.id.tv_promotion_list_item_with_presence);
                viewHolder.activeForUserIndicatorImage = (ImageView) convertView.findViewById(R.id.promotion_available_presence);
                viewHolder.promData = promData;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mainText.setText(itemsArrayList.get(position).getName());
            if (promData != null && isPromotionActiveForUser(position, promData)) {
                if (viewHolder.activeForUserIndicatorImage.getId() != ACTIVE_FOR_USER) {
                    viewHolder.activeForUserIndicatorImage.setImageResource(ACTIVE_FOR_USER);
                }
            } else {
                if (viewHolder.activeForUserIndicatorImage.getId() != NOT_ACTIVE_FOR_USER) {
                    viewHolder.activeForUserIndicatorImage.setImageResource(NOT_ACTIVE_FOR_USER);
                }
            }
            return convertView;
        }else {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.promotion_list_item_with_state_status, parent, false);

                viewHolder.mainText = (TextView)convertView.findViewById(R.id.tv_promotion_list_item_with_presence);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.mainText.setText(itemsArrayList.get(position).getName());
            return convertView;
        }
    }

    private Boolean isPromotionActiveForUser(int positionOnList, List<KawowyDzienniczekService.PromotionData> userPromData){
         return userPromData.contains(itemsArrayList.get(positionOnList));
    }
}
