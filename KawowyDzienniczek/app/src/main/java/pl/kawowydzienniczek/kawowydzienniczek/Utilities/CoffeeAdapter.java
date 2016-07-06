package pl.kawowydzienniczek.kawowydzienniczek.Utilities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.kawowydzienniczek.kawowydzienniczek.R;

public class CoffeeAdapter extends ArrayAdapter{

    private final Context context;
    private final ArrayList<CoffeeItem> itemsArrayList;

    public CoffeeAdapter(Context context, ArrayList<CoffeeItem> itemsArrayList) {
        super(context, R.layout.lw_cofee_shops_item, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemRow = inflater.inflate(R.layout.lw_cofee_shops_item,parent, false);

        TextView titleView = (TextView)itemRow.findViewById(R.id.title);
        TextView desciptionView = (TextView)itemRow.findViewById(R.id.description);
        titleView.setText(itemsArrayList.get(position).getTitle());
        desciptionView.setText(itemsArrayList.get(position).getDescription());

        return  itemRow;
    }
}
