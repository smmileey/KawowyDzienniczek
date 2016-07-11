package pl.kawowydzienniczek.kawowydzienniczek.Utilities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pl.kawowydzienniczek.kawowydzienniczek.R;
import pl.kawowydzienniczek.kawowydzienniczek.Services.HttpService;

public class ProductAdapter extends ArrayAdapter<HttpService.ProductData> {

    private final Context context;
    private final ArrayList<HttpService.ProductData> itemsArrayList;

    public ProductAdapter(Context context, ArrayList<HttpService.ProductData> itemsArrayList){
        super(context, R.layout.lw_offers, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.lw_offers, parent, false);
        }

        ImageView prodImageView = (ImageView)convertView.findViewById(R.id.product_image);
        TextView prodNameView = (TextView)convertView.findViewById(R.id.product_name);
        TextView prodDescView = (TextView)convertView.findViewById(R.id.product_description);
        TextView prodPriceTextView = (TextView)convertView.findViewById(R.id.product_price);

        prodNameView.setText(itemsArrayList.get(position).getName());
        prodDescView.setText(itemsArrayList.get(position).getDescription());
        prodPriceTextView.setText(itemsArrayList.get(position).getPrice());

        Picasso.with(context).load(itemsArrayList.get(position).getImg()).resize(150, 150).into(prodImageView);

        return  convertView;
    }
}
