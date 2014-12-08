package pl.skuteczne_inwestowanie.riskcalc;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.skuteczne_inwestowanie.riskcalc.exceptions.NoFoundCurrencyException;


public class CurrencyListActivity extends Activity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    @InjectView(R.id.etTickSize) EditText etTickSize;
    @InjectView(R.id.etTickValue) EditText etTickValue;
    @InjectView(R.id.etMinPos) EditText etMinPos;

    @InjectView(R.id.tvCurrencyRate) TextView tvCurrencyRate;
    @InjectView(R.id.etCurrencyRate) EditText etCurrencyRate;
    @InjectView(R.id.lvCurrenciesList) ListView lvCurrenciesList;

    @InjectView(R.id.ibDownloadRate) ImageButton ibDownloadRate;
    @InjectView(R.id.bConfirm) Button bConfirm;

    private ListAdapter listAdapter;
    private Position currentPosition;
    private QuotationDownloader quotationDownloader;
    private Spinner sBaseCurrency;
    private Spinner sQuotedCurrency;

    private String[] listBaseCurrencies;
    private String[] listQuotedCurrencies;
    private ArrayAdapter<String> aBaseCurrency;
    private ArrayAdapter<String> aQuotedCurrency;
    private String currentQuotedBalanceCurrencyCross;

    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);
        ButterKnife.inject(this);

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
        android.util.Log.d("Debug", "d=" + lvCurrenciesList.getDivider());

        setListeners();
        getCurrencyListsFromResource();
        initListOfPositions();
        initSpinners();
        updateFieldsFromCurrentPosition();
    }

    void setEtValue(EditText et, Double value) {
        et.setText(value.toString());
    }
    Double getEtValue(EditText et) {
        return Double.parseDouble(et.getText().toString());
    }

    private void initSpinners() {
        sBaseCurrency = (Spinner) findViewById(R.id.sBaseCurrency);
        aBaseCurrency = new ArrayAdapter<String>(this
                , R.layout.spinner_item
                , new ArrayList<String>(Arrays.asList(listBaseCurrencies)));
        sBaseCurrency.setAdapter(aBaseCurrency);
        sBaseCurrency.setOnItemSelectedListener(this);

        sQuotedCurrency = (Spinner) findViewById(R.id.sQuotedCurrency);
        aQuotedCurrency = new ArrayAdapter<String>(this
                , R.layout.spinner_item
                , new ArrayList<String>(Arrays.asList(listQuotedCurrencies)));
        sQuotedCurrency.setAdapter(aQuotedCurrency);
        sQuotedCurrency.setOnItemSelectedListener(this);
    }

    private void updateFieldsFromCurrentPosition() {

        sBaseCurrency.setSelection(aBaseCurrency.getPosition(currentPosition.getInstrument().getBaseCurrency()));
        sQuotedCurrency.setSelection(aQuotedCurrency.getPosition(currentPosition.getInstrument().getQuotedCurrency()));

        setEtValue(etTickSize, currentPosition.getInstrument().getTickSize());
        setEtValue(etTickValue, currentPosition.getInstrument().getTickValue());
        setEtValue(etMinPos, currentPosition.getInstrument().getMinPos());
    }

    void updateCurrentPositionFromFields() {
        currentPosition.getInstrument().setTickSize(getEtValue(etTickSize));
        currentPosition.getInstrument().setTickValue(getEtValue(etTickValue));
        currentPosition.getInstrument().setMinPos(getEtValue(etMinPos));
    }

    private void getCurrencyListsFromResource() {
        listBaseCurrencies = getResources().getStringArray(R.array.base_currencies_list);
        listQuotedCurrencies = getResources().getStringArray(R.array.quoted_currencies_list);
    }

    private void setListeners() {
        ibDownloadRate.setOnClickListener(this);
        bConfirm.setOnClickListener(this);
    }

    private String updateTvCurrencyRate() {
        String currencyCross = currentPosition.getInstrument().getQuotedCurrency()
                + currentPosition.getAccount().getCurrency();
        tvCurrencyRate.setText(currencyCross + " rate:");

        //updating field in the same time
        currentQuotedBalanceCurrencyCross = currencyCross;

        try {
            setEtValue(etCurrencyRate, quotationDownloader.getQuotation(currentQuotedBalanceCurrencyCross));
        } catch (NoFoundCurrencyException e) {
            e.printStackTrace();
        }

        return currencyCross;
    }

    private void readFieldsFromFile() {
        try {
            //but if I don't touch any exceptions I read position from file
            currentPosition = (Position) InternalStorage.readObject(this, Const.FILE_DEFAULT_POS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            quotationDownloader = (QuotationDownloader) InternalStorage.readObject(this, Const.FILE_QUOTATIONS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        listAdapter.readListFromFile();
    }

    private void initListOfPositions() {
        currentPosition = new Position();
        quotationDownloader = new QuotationDownloader();
        List<Position> positionsList = new ArrayList<Position>();
        listAdapter = new ListAdapter(this, R.id.lvCurrenciesList, positionsList,mTouchListener);
        readFieldsFromFile();
        lvCurrenciesList.setAdapter(listAdapter);

        //positionsList.add(new Position(account, new Instrument("USD", "RUB", 0.00001, 0.1, 0.01), 47.25617, 44.00000, 0.01));

    }


    private void loadCurrencyToSettings(int position) {
        currentPosition=listAdapter.getItem(position);
        updateFieldsFromCurrentPosition();
        listAdapter.add(currentPosition); //the simplest way to sort our list
        saveFiles();
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(CurrencyListActivity.this).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            lvCurrenciesList.requestDisallowInterceptTouchEvent(true);
                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 3) {
                            // Greater than a one-third of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        lvCurrenciesList.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(lvCurrenciesList, v);
                                        } else {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            lvCurrenciesList.setEnabled(true);
                                        }
                                    }
                                });
                    // if not swiping
                    } else {
                        int pos = lvCurrenciesList.getPositionForView(v);
                        loadCurrencyToSettings(pos);
                    }
                }
                saveFiles();
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = listAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = lvCurrenciesList.getPositionForView(viewToRemove);
        listAdapter.remove(listAdapter.getItem(position));

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = listAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        lvCurrenciesList.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    lvCurrenciesList.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
        saveFiles(); //after delete
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.currency_list, menu);
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == sBaseCurrency) updateBaseCurrency(parent, position);
        if (parent == sQuotedCurrency) updateQuotedCurrency(parent, position);
    }


    //private void updateSecondList()

    //next time I write one method instead two very similar
    private void updateBaseCurrency(AdapterView<?> parent, int pos) {
        String newCurrentBaseCurrency = parent.getItemAtPosition(pos).toString();
        currentPosition.getInstrument().setBaseCurrency(newCurrentBaseCurrency);
        updateTvCurrencyRate();
    }

    private void updateQuotedCurrency(AdapterView<?> parent, int pos) {
        String newCurrentQuotedCurrency = parent.getItemAtPosition(pos).toString();
        currentPosition.getInstrument().setQuotedCurrency(newCurrentQuotedCurrency);
        updateTvCurrencyRate();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void saveFiles() {
        try {
            InternalStorage.writeObject(this, Const.FILE_QUOTATIONS, quotationDownloader);
            InternalStorage.writeObject(this, Const.FILE_DEFAULT_POS, currentPosition);
            listAdapter.saveListToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v==ibDownloadRate) {
            updateTvCurrencyRate();
        }
        if (v==bConfirm) {
            updateCurrentPositionFromFields();
            listAdapter.add(currentPosition);
            saveFiles();
//            onBackPressed();
        }
    }

}
