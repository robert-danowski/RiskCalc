package skuteczne_inwestowanie.pl.sizeofposcalc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class MainActivity extends Activity {

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
        etSl = (EditText) findViewById(R.id.etSl);
        etSlOffset = (EditText) findViewById(R.id.etSlOffset);
        etSize = (EditText) findViewById(R.id.etSize);

        etPrice.setText(Double.toString(position.getOpenPrice()));
        etSl.setText(Double.toString(position.getSl()));
        etSlOffset.setText(Double.toString(position.calcSlOffset()));
        etSize.setText(Double.toString(position.calcSize()));

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
