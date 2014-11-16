package skuteczne_inwestowanie.pl.sizeofposcalc;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;


public class MainActivity extends Activity implements OnFocusChangeListener, OnClickListener {

    private Position position;

    private EditText etPrice;
    private EditText etSl;
    private EditText etSlOffset;
    private EditText etSize;
    private EditText etPercentRisk;
    private EditText etAmountRisk;
    private EditText etBalance;
    private Button bCalculate;
    private ImageButton bDecrease;
    private ImageButton bIncrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = new Position();

        initActivityFields();
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

//    Double getEtPriceValue() {
//        return Double.parseDouble(etPrice.getText().toString());
//    }
//
//    Double getEtSlValue() {
//        return Double.parseDouble(etSl.getText().toString());
//    }
//
//    Integer getEtSlOffsetValue() {
//        return Integer.parseInt(etSlOffset.getText().toString());
//    }
//
//    Double getEtSizeValue() {
//        return Double.parseDouble(etSize.getText().toString());
//    }
//
//    Double getEtPercentRiskValue() {
//        return Double.parseDouble(etPercentRisk.getText().toString());
//    }
//
//    Double getEtAmountRiskValue() {
//        return Double.parseDouble(etAmountRisk.getText().toString());
//    }
//
//    Double getEtBalanceValue() {
//        return Double.parseDouble(etBalance.getText().toString());
//    }

    private void initActivityFields() {
        //find view
        etPrice = (EditText) findViewById(R.id.etPrice);
        etSlOffset = (EditText) findViewById(R.id.etSlOffset);
        etSl = (EditText) findViewById(R.id.etSl);
        etSize = (EditText) findViewById(R.id.etSize);
        etPercentRisk = (EditText) findViewById(R.id.etPercentRisk);
        etAmountRisk = (EditText) findViewById(R.id.etAmountRisk);
        etBalance = (EditText) findViewById(R.id.etBalance);
        bCalculate = (Button) findViewById(R.id.bCalculate);
        bDecrease = (ImageButton) findViewById(R.id.bDecrease);
        bIncrease = (ImageButton) findViewById(R.id.bIncrease);
        //init values
        setEtValue(etPrice, position.getOpenPrice(), -(int) Math.log10(position.getInstrument().getPointSize()));
        setEtValue(etSl, position.getSl(), -(int) Math.log10(position.getInstrument().getPointSize()));
        calcEtSlOffset();
        setEtValue(etSize, position.calcSize(), -(int) Math.log10(position.getInstrument().getMinPos()));
        setEtValue(etPercentRisk, position.getAccount().getMaxRisk(), 3);
        setEtValue(etAmountRisk, position.calcMoneyAtRisk(), -(int) Math.log10(position.getAccount().getMinUnit()));
        setEtValue(etBalance, position.getAccount().getBalance(), -(int) Math.log10(position.getAccount().getMinUnit()));
        //set focus listener
        etPrice.setOnFocusChangeListener(this);
        etSlOffset.setOnFocusChangeListener(this);
        etSl.setOnFocusChangeListener(this);
        etSize.setOnFocusChangeListener(this);
        etPercentRisk.setOnFocusChangeListener(this);
        etAmountRisk.setOnFocusChangeListener(this);
        etBalance.setOnFocusChangeListener(this);
        //set onclick listener
        bCalculate.setOnClickListener(this);
        bDecrease.setOnClickListener(this);
        bIncrease.setOnClickListener(this);
    }

    public void calcEtSlOffset() {
        setEtValue(etSlOffset, (double) position.getSlOffset(), 0);
    }

    public void calcEtSize() {
        setEtValue(etSize, position.calcSize(), -(int) Math.log10(position.getInstrument().getMinPos()));
    }

    public void calcEtSl() {
        //new sl should be calculated
        setEtValue(etSl, position.getSl(), -(int) Math.log10(position.getInstrument().getPointSize()));
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
                setEtValue((EditText) v, position.getOpenPrice(), -(int) Math.log10(position.getInstrument().getPointSize()));
            }
            if (v == etSlOffset) {
                setEtValue((EditText) v, (double) position.getSlOffset(), 0);
            }
            if (v == etSl) {
                setEtValue((EditText) v, position.getSl(), -(int) Math.log10(position.getInstrument().getPointSize()));
            }
            if (v == etPercentRisk) {
                setEtValue((EditText) v, position.getAccount().getMaxRisk(), 3);
            }
            if (v == etSize) {
                setEtValue((EditText) v, position.getSize(), -(int) Math.log10(position.getInstrument().getMinPos()));
            }
            if (v == etAmountRisk) {
                setEtValue((EditText) v, position.calcMoneyAtRisk(), -(int) Math.log10(position.getAccount().getMinUnit()));
            }
            if (v == etBalance) {
                setEtValue((EditText) v, position.getAccount().getBalance(), -(int) Math.log10(position.getAccount().getMinUnit()));
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
        if ((v == bDecrease || v == bIncrease) && getCurrentFocus() instanceof EditText)
            changeEt(v);
    }

    //invoked after sure that edittext is focused
    private void changeEt(View v) {
        EditText currentEt = (EditText) getCurrentFocus();
        double prevValue = getEtValue(currentEt);
        double change = 0;
        double nextValue;
        if (currentEt == etPrice || currentEt == etSl)
            change = position.getInstrument().getPointSize();
        if (currentEt == etSlOffset) change = 1;
        if (currentEt == etSize) change = position.getInstrument().getMinPos();
        if (currentEt == etPercentRisk) change = 0.001;
        if (currentEt == etAmountRisk || currentEt == etBalance)
            change = position.getAccount().getMinUnit();

        if (v == bDecrease) setEtValue(currentEt, prevValue - change, -(int) Math.log10(change));
        if (v == bIncrease) setEtValue(currentEt, prevValue + change, -(int) Math.log10(change));
    }

    //invoked after sure that edittext is focused
    private void increaseEt() {

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
}
