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
import java.util.HashMap;
import java.util.List;

import static pl.skuteczne_inwestowanie.riskcalc.InternalStorage.readObject;

/**
 * Created by teodor on 2014-11-19.
 */
public class ListAdapter extends ArrayAdapter<Position> implements Serializable {
    private Activity context;
    private List<Position> list;
    private List<Integer> idForItems;
    private int maxSizeOfList = 5; //someday it could be editable from application
    static int currentId = 1;

    HashMap<Position, Integer> mIdMap = new HashMap<Position, Integer>();
    View.OnTouchListener mTouchListener;

//    public ListAdapter(Context context, int resource, List<Position> objects) {
//        super(context, resource, objects);
//        this.context = (Activity) context;
//        list = objects;
//    }

    public ListAdapter(Context context, int textViewResourceId,
                       List<Position> objects, View.OnTouchListener listener) {
        super(context, textViewResourceId, objects);
        this.context = (Activity) context;
        list=objects;
        mTouchListener = listener;
        for (Position object : objects) {
            mIdMap.put(object, currentId++);
        }
    }

    @Override
    public long getItemId(int position) {
        Position item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //I know linked list would be better for this but I have small amount of data
    private void addAsFirstPosition(Position position) {
        List<Position> tempList = new ArrayList<Position>();
        tempList.add(position);
        mIdMap.put(position, currentId++);
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
                if (list.size() == maxSizeOfList) list.remove(maxSizeOfList - 1);
            }
        }
        if (!thereIs)
//            list.add(position); //the older approach
            addAsFirstPosition(position);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Position position) {
        super.remove(position);
        list.remove(position);
    }

    public void saveListToFile() {
        try {
            InternalStorage.writeObject(context, Const.FILE_CURR_SET_LIST, list);
            InternalStorage.writeObject(context, Const.FILE_CURR_SET_MAP, mIdMap);
            InternalStorage.writeObject(context, Const.FILE_CURR_ID, currentId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readListFromFile() {
        ArrayList<Position> tempList = null;
        HashMap<Position, Integer> tempMap = null;

        try {
            tempList = (ArrayList<Position>) readObject(context, Const.FILE_CURR_SET_LIST);
            tempMap = (HashMap<Position, Integer>) readObject(context, Const.FILE_CURR_SET_MAP);
            currentId = (Integer) readObject(context, Const.FILE_CURR_ID);
            if (tempList != null) list = tempList;
            if (tempMap != null) mIdMap = tempMap;

            notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = super.getView(position, convertView, parent);
//        if (view != convertView) {
//            // Add touch listener to every new view to track swipe motion
////            view.setOnTouchListener(mTouchListener);
//        }
//        View rowView=view;

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.item_row, null);
            rowView.setOnTouchListener(mTouchListener);
        }

        final Position cursorPosition = list.get(position);

        TextView bC = (TextView) rowView.findViewById(R.id.tvBaseCurrency);
        TextView bQ = (TextView) rowView.findViewById(R.id.tvQuotedCurrency);
        bC.setText(cursorPosition.getInstrument().getBaseCurrency());
        bQ.setText(cursorPosition.getInstrument().getQuotedCurrency());

        return rowView;
    }
}

