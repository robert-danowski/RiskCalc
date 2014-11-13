package skuteczne_inwestowanie.pl.sizeofposcalc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnFocusChangeListener {

    private Position position;

    private EditText etPrice;
    private EditText etSl;
    private EditText etSlOffset;
    private EditText etSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = new Position();

        etPrice = (EditText) findViewById(R.id.etPrice);
        etPrice.setOnFocusChangeListener(this);
        etSl = (EditText) findViewById(R.id.etSl);
        etSlOffset = (EditText) findViewById(R.id.etSlOffset);
        etSize = (EditText) findViewById(R.id.etSize);

        etPrice.setText(Double.toString(position.getOpenPrice()));
        etSl.setText(Double.toString(position.getSl()));
        calcEtOffset();
        etSize.setText(Double.toString(position.calcSize()));

    }

    public void calcEtOffset() {
        etSlOffset.setText(Integer.toString(position.calcSlOffset()));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v==etPrice && !hasFocus) {
            //user probably changed open price, so we take it
            position.setOpenPrice(etPrice.getText().toString());
            calcEtOffset();
        }
    }
}
