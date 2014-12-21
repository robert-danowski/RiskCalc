package pl.skuteczne_inwestowanie.riskcalc;

import android.os.AsyncTask;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import pl.skuteczne_inwestowanie.riskcalc.exceptions.CurrencyNotFoundException;

/**
 * Created by teodor on 2014-11-17.
 */
public class QuotationDownloader implements Serializable {

    //list of downloaded currencies
    private List<DownloadedCurrency> listOfCurrencies;

    private class DownloadedCurrency implements Serializable {

        private String name;
        private double quotation;

        private DownloadedCurrency(String name, double quotation) {
            this.name = name;
            this.quotation = quotation;
        }

        public String getName() {
            return name;
        }

        public double getQuotation() {
            return quotation;
        }

        public void setQuotation(double quotation) {
            this.quotation = quotation;
        }
    }


    QuotationDownloader() {
        listOfCurrencies = new ArrayList<DownloadedCurrency>();
    }

    QuotationDownloader(ArrayList<DownloadedCurrency> list) {
        listOfCurrencies=list;
    }

    //only for async tasks
    private double downloadQuotation(String param) {
        double result = 0;
        try {

            URL stooq = new URL("http://stooq.pl/q/l/?s="
                    + param + "&e=csv");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(stooq.openStream()));

            String inputLine;
            String[] tokens = {""};
            while ((inputLine = in.readLine()) != null)
//                    Symbol,Data,Czas,Otwarcie,Najwyzszy,Najnizszy,Zamkniecie
                tokens = inputLine.split(",");

            try {
                result = Double.parseDouble(tokens[6]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //only for internal purposes
    private void setQuotation(String what,double howMuch) {
        boolean thereIs = false;
        for (int i = 0; i < listOfCurrencies.size(); i++) {
            if (listOfCurrencies.get(i).getName().equalsIgnoreCase(what)) {
                thereIs = true;
                listOfCurrencies.get(i).setQuotation(howMuch);
            }
        }
        if (!thereIs) listOfCurrencies.add(new DownloadedCurrency(what, howMuch));
    }

    private void setQuotation(DownloadedCurrency dc) {
        setQuotation(dc.getName(),dc.getQuotation());
    }

    //avoiding waiting for every download, but data are delayed
    public double getQuotation(String what) {

        //every getting causes new downloading
        if (!(what.substring(0,3).equalsIgnoreCase(what.substring(3,6)))) new UpdateTask().execute(what);

        boolean thereIs = false;
        double result = 0;
        for (int i = 0; i < listOfCurrencies.size(); i++) {
            if (listOfCurrencies.get(i).getName().equalsIgnoreCase(what)) {
                thereIs = true;
                result = listOfCurrencies.get(i).getQuotation();
            }
        }
        if (!thereIs) return 1.0;

        return result;
    }

    private class UpdateTask extends AsyncTask<String, Void, DownloadedCurrency> {

        @Override
        protected DownloadedCurrency doInBackground(String... params) {
            //if base and quoted currency are equal there are not in stooq.pl
            if (params[0].substring(0,3).equalsIgnoreCase(params[0].substring(3,6)))
                return new DownloadedCurrency(params[0], 1.0);
            else return new DownloadedCurrency(params[0],downloadQuotation(params[0]));
        }

        @Override
        protected void onPostExecute(DownloadedCurrency downloadedCurrency) {
            setQuotation(downloadedCurrency);
        }
    }

    public void updateETOpenPrice(MainActivity ma, EditText et, Instrument ins) {
        UpdateTask updateTask = new UpdateTask();
        updateTask.execute(ins.getBaseCurrency()+ins.getQuotedCurrency());
        ma.setEtValue(et, getQuotation(ins.getBaseCurrency()+ins.getQuotedCurrency()), -(int) Math.log10(ins.getTickSize()));
        ma.rememberOpenPrice();
    }

    public void updateCurrency(String  currencyString) {
        UpdateTask updateTask = new UpdateTask();
        updateTask.execute(currencyString);
    }

    public void updateCurrency(DownloadedCurrency dc) {
        updateCurrency(dc.getName());
    }

    public void updateCurrency(Position pos) {
        updateCurrency(pos.getInstrument().getBaseCurrency() + pos.getInstrument().getQuotedCurrency());
    }

    public void updateAllCurrencies() {
        for (DownloadedCurrency dc : listOfCurrencies) {
            updateCurrency(dc);
        }
    }

}
