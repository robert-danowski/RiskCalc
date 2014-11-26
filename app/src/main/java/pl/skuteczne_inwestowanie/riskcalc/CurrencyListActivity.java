package pl.skuteczne_inwestowanie.riskcalc;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class CurrencyListActivity extends Activity {

    EditText etTickSize;
    EditText etTickValue;
    EditText etMinPos;

    TextView tvCurrencyRate;
    EditText etCurrencyRate;
    ListView lvCurrenciesList;

    List<Position> positionsList=new ArrayList<Position>();
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);
        //overridePendingTransition(R.anim.left_in,R.anim.left_out);

        initActivityFields();
        initListOfPositions();
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

    private void initListOfPositions() {
        Account account = new Account();
        Instrument instrument = new Instrument();
        positionsList.add(new Position(account,instrument, 1.2486, 1.2466, 0.01));
        positionsList.add(new Position(account,new Instrument("USD","RUB",0.00001,0.1,0.01), 47.25617, 44.00000, 0.01));
        lvCurrenciesList = (ListView) findViewById(R.id.listView);
        listAdapter = new ListAdapter(this, R.id.listView, positionsList);
        lvCurrenciesList.setAdapter(listAdapter);
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
                overridePendingTransition(R.anim.right_in,R.anim.right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }
}
