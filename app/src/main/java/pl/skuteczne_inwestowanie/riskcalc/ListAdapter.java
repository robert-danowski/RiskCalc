package pl.skuteczne_inwestowanie.riskcalc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import skuteczne_inwestowanie.pl.riskcalc.R;

/**
 * Created by teodor on 2014-11-19.
 */
public class ListAdapter extends ArrayAdapter<Position> {
    private Activity context;
    private List<Position> list;

    public void setList(List<Position> list) {
        this.list = list;
    }

    public ListAdapter(Context context, int resource, List<Position> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;


        if (rowView==null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.item_row, null);
        }

        final Position currentPosition = list.get(position);

        TextView bC = (TextView) rowView.findViewById(R.id.tvBaseCurrency);
        TextView bQ = (TextView) rowView.findViewById(R.id.tvQuotedCurrency);
        bC.setText(currentPosition.getInstrument().getBaseCurrency());
        bQ.setText(currentPosition.getInstrument().getQuotedCurrency());

        return rowView;
    }
}

