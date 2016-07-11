package pl.kawowydzienniczek.kawowydzienniczek.Utilities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.kawowydzienniczek.kawowydzienniczek.Globals.CoffeeAvailableData;
import pl.kawowydzienniczek.kawowydzienniczek.R;

public class CoffeeAvailableAdapter extends ArrayAdapter<CoffeeAvailableData>{

    private final Context context;
    private final ArrayList<CoffeeAvailableData> itemsArrayList;

    public CoffeeAvailableAdapter(Context context, ArrayList<CoffeeAvailableData> itemsArrayList) {
        super(context, R.layout.lw_cofee_show_preview, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.lw_cofee_show_preview,parent, false);
        }

        TextView titleView = (TextView)convertView.findViewById(R.id.title);
        TextView descriptionView = (TextView)convertView.findViewById(R.id.description);
        titleView.setText(itemsArrayList.get(position).getTitle());
        descriptionView.setText(itemsArrayList.get(position).getDescription());

        return convertView;
    }
}
