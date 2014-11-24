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
        long startTime = System.nanoTime();
        changeEtThread();
        //some wait after start continuous incrementation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int multiplier=1;
        while (!this.isSuspended()) {
            long currentTime = System.nanoTime();
            //calculate multiplier for faster incrementation number of seconds square and divide by 9
            //assure as we could keep the same tempo for 3 seconds
            multiplier = (int)(Math.max((Math.pow((currentTime-startTime)/1e9,2)/9),1));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            changeEtThread(multiplier);
        }
    }

    private void changeEtThread(final double multiplier) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                context.changeEt(holdedButton,multiplier);
            }
        });
    }
    private void changeEtThread() {changeEtThread(1);}
}
