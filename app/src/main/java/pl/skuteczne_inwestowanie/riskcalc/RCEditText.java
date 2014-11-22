package pl.skuteczne_inwestowanie.riskcalc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by teodor on 2014-11-14.
 */
public class RCEditText extends EditText {

    public RCEditText(Context context) {
        super(context);
        init();
    }

    public RCEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RCEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // set your input filter here
    }

    public void setText(String text) {
        super.setText(text);

    }
}
