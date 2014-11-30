package pl.skuteczne_inwestowanie.riskcalc;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.List;

import pl.skuteczne_inwestowanie.riskcalc.exceptions.NoFoundCurrencyException;


public class CurrencyListActivity extends Activity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    private EditText etTickSize;
    private EditText etTickValue;
    private EditText etMinPos;

    private TextView tvCurrencyRate;
    private EditText etCurrencyRate;
    private ListView lvCurrenciesList;

    private ImageButton ibDownloadRate;
    private Button bConfirm;

    private List<Position> positionsList;
    private ListAdapter listAdapter;
    private Position currentPosition;
    private QuotationDownloader quotationDownloader;
    private Spinner sBaseCurrency;
    private Spinner sQuotedCurrency;

    private String currentBaseCurrency;
    private String currentQuotedCurrency;
    private String[] listBaseCurrencies;
    private String[] listQuotedCurrencies;
    private ArrayAdapter<String> aBaseCurrency;
    private ArrayAdapter<String> aQuotedCurrency;
    private String currentQuotedBalanceCurrencyCross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        initListOfPositions();
        initActivityFields();
    }

    void setEtValue(EditText et, Double value) {
        et.setText(value.toString());
    }



    private void initActivityFields() {
        etTickSize = (EditText) findViewById(R.id.etTickSize);
        etTickValue = (EditText) findViewById(R.id.etTickValue);
        etMinPos = (EditText) findViewById(R.id.etMinPos);
        tvCurrencyRate = (TextView) findViewById(R.id.tvCurrencyRate);
        etCurrencyRate = (EditText) findViewById(R.id.etCurrencyRate);
        ibDownloadRate = (ImageButton) findViewById(R.id.ibDownloadRate);
        bConfirm = (Button) findViewById(R.id.bConfirm);

        listBaseCurrencies = getResources().getStringArray(R.array.base_currencies_list);
        listQuotedCurrencies = getResources().getStringArray(R.array.quoted_currencies_list);

        sBaseCurrency = (Spinner) findViewById(R.id.sBaseCurrency);
        aBaseCurrency = new ArrayAdapter<String>(this
                , android.R.layout.simple_spinner_item
                , new ArrayList<String>(Arrays.asList(listBaseCurrencies)));
        sBaseCurrency.setAdapter(aBaseCurrency);
        sBaseCurrency.setSelection(aBaseCurrency.getPosition(currentPosition.getInstrument().getBaseCurrency()));
        sBaseCurrency.setOnItemSelectedListener(this);

        sQuotedCurrency = (Spinner) findViewById(R.id.sQuotedCurrency);
        aQuotedCurrency = new ArrayAdapter<String>(this
                , android.R.layout.simple_spinner_item
                , new ArrayList<String>(Arrays.asList(listQuotedCurrencies)));
        sQuotedCurrency.setAdapter(aQuotedCurrency);
        sQuotedCurrency.setSelection(aQuotedCurrency.getPosition(currentPosition.getInstrument().getQuotedCurrency()));
        sQuotedCurrency.setOnItemSelectedListener(this);

        setEtValue(etTickSize, currentPosition.getInstrument().getTickSize());
        setEtValue(etTickValue, currentPosition.getInstrument().getTickValue());
        setEtValue(etMinPos, currentPosition.getInstrument().getMinPos());

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

    private void initListOfPositions() {
        Account account = new Account();
        Instrument instrument = new Instrument();
        currentPosition = new Position();
        quotationDownloader = new QuotationDownloader();
        try {
            //but if I don't touch any exceptions I read position from file
            currentPosition = (Position) InternalStorage.readObject(this, Const.FILE_DEFAULT_POS);
            quotationDownloader = (QuotationDownloader) InternalStorage.readObject(this, Const.FILE_QUOTATIONS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        positionsList = new ArrayList<Position>();
        positionsList.add(currentPosition);
//        positionsList.add(new Position(account, instrument, 1.2486, 1.2466, 0.01));
        positionsList.add(new Position(account, new Instrument("USD", "RUB", 0.00001, 0.1, 0.01), 47.25617, 44.00000, 0.01));
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

    @Override
    public void onClick(View v) {
        if (v==ibDownloadRate) {
            updateTvCurrencyRate();
        }
        if (v==bConfirm) {
            try {
                InternalStorage.writeObject(this, Const.FILE_QUOTATIONS, quotationDownloader);
                InternalStorage.writeObject(this, Const.FILE_DEFAULT_POS, currentPosition);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onBackPressed();
        }
    }

}
