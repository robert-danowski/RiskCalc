package pl.skuteczne_inwestowanie.riskcalc;

import android.app.Activity;
import android.widget.ImageButton;

/**
 * Created by teodor on 2014-11-23.
 */
class IncrementationThread extends Thread {

    final MainActivity context;
    final ImageButton holdedButton;
    boolean suspended = false;

    IncrementationThread(MainActivity context, ImageButton holdedButton) {
        this.context = context;
        this.holdedButton = holdedButton;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    @Override
    public void run() {
        while (!this.isSuspended()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.runOnUiThread(new Runnable() {
                public void run() {
                    context.changeEt(holdedButton);
                }
            });
        }
    }
}
