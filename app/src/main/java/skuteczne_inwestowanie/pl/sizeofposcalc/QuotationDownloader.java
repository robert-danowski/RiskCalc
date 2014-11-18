package skuteczne_inwestowanie.pl.sizeofposcalc;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by teodor on 2014-11-17.
 */
public class QuotationDownloader {

    class Task extends AsyncTask<String, Void, Double> {

        @Override
        protected Double doInBackground(String... params) {
            double result = 0;
            try {

                URL stooq = new URL("http://stooq.pl/q/l/?s="
                        + params[0] + "&e=csv");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(stooq.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    ;
//                    Symbol,Data,Czas,Otwarcie,Najwyzszy,Najnizszy,Zamkniecie

                String[] tokens = inputLine.split(",");
                result = Double.parseDouble(tokens[6]);

                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }



    public void test() {
        Task task = new Task();
        task.execute("EURUSD");
    }
}
