package pl.skuteczne_inwestowanie.riskcalc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;


public class MainActivity extends Activity implements OnFocusChangeListener, OnClickListener, AdapterView.OnItemSelectedListener {

    private Position position;

    private EditText etPrice;
    private EditText etSl;
    private EditText etSlOffset;
    private EditText etSize;
    private EditText etPercentRisk;
    private EditText etAmountRisk;
    private EditText etBalance;
    private Spinner sCurrency;
    private Button bCalculate;
    private ImageButton ibDecrease;
    private ImageButton ibIncrease;
    private ImageButton ibDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = new Position();

        initActivityFields();
        initSpinner();
    }

    private void initSpinner() {
        sCurrency = (Spinner) findViewById(R.id.sCurrency);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_currencies_list, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        sCurrency.setAdapter(adapter);
        sCurrency.setOnItemSelectedListener(this);
    }

    private void initActivityFields() {
        findViews();
        initListeners();
        initValues();
    }

    private void findViews() {
        etPrice = (EditText) findViewById(R.id.etPrice);
        etSlOffset = (EditText) findViewById(R.id.etSlOffset);
        etSl = (EditText) findViewById(R.id.etSl);
        etSize = (EditText) findViewById(R.id.etSize);
        etPercentRisk = (EditText) findViewById(R.id.etPercentRisk);
        etAmountRisk = (EditText) findViewById(R.id.etAmountRisk);
        etBalance = (EditText) findViewById(R.id.etBalance);
        bCalculate = (Button) findViewById(R.id.bCalculate);
        ibDecrease = (ImageButton) findViewById(R.id.ibDecrease);
        ibIncrease = (ImageButton) findViewById(R.id.ibIncrease);
        ibDownload = (ImageButton) findViewById(R.id.ibDownload);
    }

    private void initListeners() {
        //set focus listeners
        etPrice.setOnFocusChangeListener(this);
        etSlOffset.setOnFocusChangeListener(this);
        etSl.setOnFocusChangeListener(this);
        etSize.setOnFocusChangeListener(this);
        etPercentRisk.setOnFocusChangeListener(this);
        etAmountRisk.setOnFocusChangeListener(this);
        etBalance.setOnFocusChangeListener(this);
        //set onclick listeners
        bCalculate.setOnClickListener(this);
        ibDecrease.setOnClickListener(this);
        ibIncrease.setOnClickListener(this);
        ibDownload.setOnClickListener(this);
    }


    void setEtValue(EditText et, Double value, int decimalPlaces) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setGroupingUsed(false);
        if (decimalPlaces <= 0) {
            nf.setParseIntegerOnly(true);
            nf.setMinimumIntegerDigits(-decimalPlaces);
        } else {
            nf.setParseIntegerOnly(false);
            nf.setMaximumFractionDigits(decimalPlaces);
            nf.setMinimumFractionDigits(decimalPlaces);
        }
        et.setText(nf.format(value));
        animateTextView(et);
    }

    Double getEtValue(EditText et) {
        return Double.parseDouble(et.getText().toString());
    }

    private void initValues() {
        setEtValue(etPrice, position.getOpenPrice(), -(int) Math.log10(position.getInstrument().getTickSize()));
        setEtValue(etSl, position.getSl(), -(int) Math.log10(position.getInstrument().getTickSize()));
        calcEtSlOffset();
        setEtValue(etSize, position.calcSize(), -(int) Math.log10(position.getInstrument().getMinPos()));
        setEtValue(etPercentRisk, position.getAccount().getMaxRisk(), 3);
        setEtValue(etAmountRisk, position.calcMoneyAtRisk(), -(int) Math.log10(position.getAccount().getMinUnit()));
        setEtValue(etBalance, position.getAccount().getBalance(), -(int) Math.log10(position.getAccount().getMinUnit()));
    }

    public void calcEtSlOffset() {
        setEtValue(etSlOffset, (double) position.getSlOffset(), 0);
    }

    public void calcEtSize() {
        setEtValue(etSize, position.calcSize(), -(int) Math.log10(position.getInstrument().getMinPos()));
    }

    public void calcEtSl() {
        //new sl should be calculated
        setEtValue(etSl, position.getSl(), -(int) Math.log10(position.getInstrument().getTickSize()));
    }

    public void calcEtAmountRisk() {
        setEtValue(etAmountRisk, position.calcMoneyAtRisk(), -(int) Math.log10(position.getAccount().getMinUnit()));
    }

    public void calcEtPercentRisk() {
        setEtValue(etPercentRisk, position.calcPercentRisk(), 3);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //when we leave field without calculation we restore previous
        if (!hasFocus) {
            if (v == etPrice) {
                //setEtValue((EditText) v, position.getOpenPrice(), -(int) Math.log10(position.getInstrument().getTickSize()));
                position.setOpenPrice(getEtValue((EditText)v));
            }
            if (v == etSlOffset) {
                //setEtValue((EditText) v, (double) position.getSlOffset(), 0);
                position.setSlOffset(getEtValue((EditText)v).intValue());
            }
            if (v == etSl) {
                //setEtValue((EditText) v, position.getSl(), -(int) Math.log10(position.getInstrument().getTickSize()));
                position.setSl(getEtValue((EditText)v));
            }
            if (v == etPercentRisk) {
                //setEtValue((EditText) v, position.getAccount().getMaxRisk(), 3);
                position.getAccount().setMaxRisk(getEtValue((EditText)v));
            }
            if (v == etSize) {
                //setEtValue((EditText) v, position.getSize(), -(int) Math.log10(position.getInstrument().getMinPos()));
                position.setSize(getEtValue((EditText)v));
            }
            if (v == etAmountRisk) {
                //setEtValue((EditText) v, position.calcMoneyAtRisk(), -(int) Math.log10(position.getAccount().getMinUnit()));
                position.setAmountRisk(getEtValue((EditText)v));
            }
            if (v == etBalance) {
                //setEtValue((EditText) v, position.getAccount().getBalance(), -(int) Math.log10(position.getAccount().getMinUnit()));
                position.getAccount().setBalance(getEtValue((EditText)v));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == bCalculate) {
            if (etPrice.isFocused()) {
                //user probably changed open price, so we take it
                position.setOpenPrice(getEtValue(etPrice));
                calcEtSlOffset();
                calcEtSize();
                calcEtAmountRisk();
            }
            if (etSlOffset.isFocused()) {
                position.setSlOffset(getEtValue(etSlOffset).intValue());
                calcEtSl();
                calcEtSize();
                calcEtAmountRisk();
            }
            if (etSl.isFocused()) {
                position.setSl(getEtValue(etSl));
                calcEtSlOffset();
                calcEtSize();
                calcEtAmountRisk();
            }
            if (etPercentRisk.isFocused()) {
                position.getAccount().setMaxRisk(getEtValue(etPercentRisk));
                calcEtSize();
                calcEtAmountRisk();
            }
            if (etSize.isFocused()) {
                position.setSize(getEtValue(etSize));
                calcEtPercentRisk();
                calcEtAmountRisk();
            }
            if (etAmountRisk.isFocused()) {
                position.setAmountRisk(getEtValue(etAmountRisk));
                calcEtSize();
                calcEtPercentRisk();

            }
            if (etBalance.isFocused()) {
                position.getAccount().setBalance(getEtValue(etBalance));
                calcEtSize();
                calcEtAmountRisk();
            }
        }
        if ((v == ibDecrease || v == ibIncrease) && getCurrentFocus() instanceof EditText)
            changeEt(v);
        if (v == ibDownload) {
            QuotationDownloader qd = new QuotationDownloader();
            qd.updateET(this, etPrice, position.getInstrument());

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == sCurrency) {
            this.position.getAccount().setCurrency(((TextView) view).getText().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //invoked after sure that edittext is focused
    private void changeEt(View v) {
        EditText currentEt = (EditText) getCurrentFocus();
        double prevValue = getEtValue(currentEt);
        double change = 0;
        if (currentEt == etPrice || currentEt == etSl)
            change = position.getInstrument().getTickSize();
        if (currentEt == etSlOffset) change = 1;
        if (currentEt == etSize) change = position.getInstrument().getMinPos();
        if (currentEt == etPercentRisk) change = 0.001;
        if (currentEt == etAmountRisk || currentEt == etBalance)
            change = position.getAccount().getMinUnit();

        if (v == ibDecrease) setEtValue(currentEt, prevValue - change, -(int) Math.log10(change));
        if (v == ibIncrease) setEtValue(currentEt, prevValue + change, -(int) Math.log10(change));
    }

    private class AnimateCalculatedFields extends AsyncTask<TextView, Integer, Void> {

        TextView animatedTextView;

        @Override
        protected Void doInBackground(TextView... params) {

            animatedTextView = params[0];

            for (int i = 0; i < 255; i += 2) {
                sleepAndPublish(i);
            }
            for (int i = 255; i >= 0; i -= 2) {
                sleepAndPublish(i);
            }

            return null;
        }

        private void sleepAndPublish(int i) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            animatedTextView.setTextColor(Color.rgb(0, values[0], 0));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            animatedTextView.setTextColor(Color.BLACK);
        }
    }

    private void animateTextView(TextView textView) {
        AnimateCalculatedFields animateCalculatedFieldsPrev = (AnimateCalculatedFields) textView.getTag();
        if (animateCalculatedFieldsPrev != null) {
            animateCalculatedFieldsPrev.cancel(true);
        }

        AnimateCalculatedFields animateCalculatedFields = new AnimateCalculatedFields();
        //animateCalculatedFields.execute(textView);
        animateCalculatedFields.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, textView);
        textView.setTag(animateCalculatedFields); //this is important for first lines of this methods
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, CurrencyListActivity.class);
            //intent.putExtra
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


