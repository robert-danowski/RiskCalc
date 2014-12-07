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
import java.util.Collections;
import java.util.List;

import static pl.skuteczne_inwestowanie.riskcalc.InternalStorage.readObject;

/**
 * Created by teodor on 2014-11-19.
 */
public class ListAdapter extends ArrayAdapter<Position> implements Serializable {
    private Activity context;
    private List<Position> listOfPositions;
    private List<Integer> listOfIds;
    private int maxSizeOfList = 5; //someday it could be editable from application
    static int currentId = 1;

    View.OnTouchListener mTouchListener;

//    public ListAdapter(Context context, int resource, List<Position> objects) {
//        super(context, resource, objects);
//        this.context = (Activity) context;
//        listOfPositions = objects;
//    }

    public ListAdapter(Context context, int textViewResourceId,
                       List<Position> objects, View.OnTouchListener listener) {
        super(context, textViewResourceId, objects);
        this.context = (Activity) context;
        listOfPositions =objects;
        mTouchListener = listener;
        listOfIds = new ArrayList<Integer>();
        for (int i = 0; i < objects.size(); i++) {
            listOfIds.add(currentId++);
        }
    }

    @Override
    public long getItemId(int position) {
        return listOfIds.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //I know linked listOfPositions would be better for this but I have small amount of data
    private void addAsFirstPosition(Position position) {
        List<Position> tempList = new ArrayList<Position>();
        List<Integer> tempIdList = new ArrayList<Integer>();
        tempList.add(position);
        tempIdList.add(currentId++);
        tempList.addAll(listOfPositions);
        tempIdList.addAll(listOfIds);
        listOfPositions = tempList;
        listOfIds = tempIdList;
    }

    @Override
    public int getCount() {
        return listOfPositions.size();
    }

    @Override
    public Position getItem(int position) {
        return listOfPositions.get(position);
    }

    @Override
    public void add(Position pos) {
        Position position = new Position(pos); //better idea is create copy of object (not only reference)
        boolean thereIs = false;
        String curBasCurr = position.getInstrument().getBaseCurrency();
        String curQuoCurr = position.getInstrument().getQuotedCurrency();

        for (int i = 0; i < listOfPositions.size(); i++) {
            String baseCurrency = listOfPositions.get(i).getInstrument().getBaseCurrency();
            String quotedCurrency = listOfPositions.get(i).getInstrument().getQuotedCurrency();

            if (baseCurrency.equalsIgnoreCase(curBasCurr)
                    && quotedCurrency.equalsIgnoreCase(curQuoCurr)) {
                thereIs = true;
//                listOfPositions.set(i,position); //the older approach
                listOfPositions.remove(i); //remove old version
                addAsFirstPosition(position); //new version on the beginning
                //delete last element if listOfPositions is too long
                if (listOfPositions.size() == maxSizeOfList) listOfPositions.remove(maxSizeOfList - 1);
            }
        }
        if (!thereIs)
//            listOfPositions.add(position); //the older approach
            addAsFirstPosition(position);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Position position) {
        super.remove(position);
        listOfPositions.remove(position);
    }

    public void saveListToFile() {
        try {
            InternalStorage.writeObject(context, Const.FILE_CURR_SET_LIST, listOfPositions);
            InternalStorage.writeObject(context, Const.FILE_CURR_SET_MAP, listOfIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readListFromFile() {
        ArrayList<Position> tempList = null;
        ArrayList<Integer> tempMap = null;

        try {
            tempList = (ArrayList<Position>) readObject(context, Const.FILE_CURR_SET_LIST);
            tempMap = (ArrayList<Integer>) readObject(context, Const.FILE_CURR_SET_MAP);
            if (tempList != null) listOfPositions = tempList;
            if (tempMap != null) listOfIds = tempMap;
            currentId = Collections.max(listOfIds)+1;
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

        final Position cursorPosition = listOfPositions.get(position);

        TextView bC = (TextView) rowView.findViewById(R.id.tvBaseCurrency);
        TextView bQ = (TextView) rowView.findViewById(R.id.tvQuotedCurrency);
        bC.setText(cursorPosition.getInstrument().getBaseCurrency());
        bQ.setText(cursorPosition.getInstrument().getQuotedCurrency());

        return rowView;
    }
}

