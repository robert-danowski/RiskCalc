package pl.skuteczne_inwestowanie.riskcalc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by teodor on 2014-11-19.
 */
public class ListAdapter extends ArrayAdapter<Position> implements Serializable {
    private Activity context;
    private List<Position> list;
    private int maxSizeOfList = 5; //someday it could be editable from application


    //I know linked list would be better for this but I have small amount of data
    private void addAsFirstPosition(Position position) {
        List<Position> tempList = new ArrayList<Position>();
        tempList.add(position);
        tempList.addAll(list);
        list = tempList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Position getItem(int position) {
        return list.get(position);
    }

    @Override
    public void add(Position pos) {
        Position position = new Position(pos); //better idea is create copy of object (not only reference)
        boolean thereIs = false;
        String curBasCurr = position.getInstrument().getBaseCurrency();
        String curQuoCurr = position.getInstrument().getQuotedCurrency();

        for (int i = 0; i < list.size(); i++) {
            String baseCurrency = list.get(i).getInstrument().getBaseCurrency();
            String quotedCurrency = list.get(i).getInstrument().getQuotedCurrency();

            if (baseCurrency.equalsIgnoreCase(curBasCurr)
                    && quotedCurrency.equalsIgnoreCase(curQuoCurr)) {
                thereIs = true;
//                list.set(i,position); //the older approach
                list.remove(i); //remove old version
                addAsFirstPosition(position); //new version on the beginning
                //delete last element if list is too long
                if (list.size()==maxSizeOfList) list.remove(maxSizeOfList - 1);
            }
        }
        if (!thereIs)
//            list.add(position); //the older approach
            addAsFirstPosition(position);
        notifyDataSetChanged();
    }

    public ListAdapter(Context context, int resource, List<Position> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        list = objects;
    }

    public void saveListToFile() {
        try {
            InternalStorage.writeObject(context, Const.FILE_CURR_SET_LIST, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readListFromFile() {
        try {
            list = (ArrayList<Position>) InternalStorage.readObject(context, Const.FILE_CURR_SET_LIST);
            notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;


        if (rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.item_row, null);
        }

        final Position cursorPosition = list.get(position);

        TextView bC = (TextView) rowView.findViewById(R.id.tvBaseCurrency);
        TextView bQ = (TextView) rowView.findViewById(R.id.tvQuotedCurrency);
        bC.setText(cursorPosition.getInstrument().getBaseCurrency());
        bQ.setText(cursorPosition.getInstrument().getQuotedCurrency());

        return rowView;
    }
}

