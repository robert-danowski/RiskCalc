package pl.skuteczne_inwestowanie.riskcalc;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;



public class CurrencyListActivity extends Activity {

    EditText etTickSize;
    EditText etTickValue;
    EditText etMinPos;

    TextView tvCurrencyRate;
    EditText etCurrencyRate;

    List<Position> positionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        initActivityFields();
    }

    private void initActivityFields() {
        etTickSize = (EditText) findViewById(R.id.etTickSize);
        etTickValue = (EditText) findViewById(R.id.etTickValue);
        etMinPos = (EditText) findViewById(R.id.etMinPos);
        tvCurrencyRate = (TextView)findViewById(R.id.tvCurrencyRate);
        etCurrencyRate = (EditText) findViewById(R.id.etCurrencyRate);
        //temporary
        etTickSize.setText("0.0001");
        etTickValue.setText("10");
        etMinPos.setText("0.01");
        tvCurrencyRate.setText("USDPLN rate:");
        etCurrencyRate.setText("3.3947");
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
