package skuteczne_inwestowanie.pl.sizeofposcalc;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity implements OnFocusChangeListener,OnClickListener {

    private Position position;

    private EditText etPrice;
    private EditText etSl;
    private EditText etSlOffset;
    private EditText etSize;
    private EditText etPercentRisk;
    private EditText etAmountRisk;
    private EditText etBalance;
    private Button bCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = new Position();

        initActivityFields();

        etPrice.setText(Double.toString(position.getOpenPrice()));
        etSl.setText(Double.toString(position.getSl()));
        calcEtOffset();
        etSize.setText(Double.toString(position.calcSize()));

    }

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
    }

    public void calcEtOffset() {
        etSlOffset.setText(Integer.toString(position.calcSlOffset()));
        animateTextView(etSlOffset);
    }

    public void calcEtSize() {
        etSize.setText(Double.toString(position.calcSize()));
        animateTextView(etSize);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == etPrice && !hasFocus) {
            //when we leave field we need to remember it
            position.setOpenPrice(etPrice.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        if (v==bCalculate) {
            if (etPrice.isFocused()) {
                //user probably changed open price, so we take it
                position.setOpenPrice(etPrice.getText().toString());
                calcEtOffset();
                calcEtSize();
            }
        }
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
        animateCalculatedFields.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,textView);
        textView.setTag(animateCalculatedFields); //this is important for first lines of this methods
    }
}
