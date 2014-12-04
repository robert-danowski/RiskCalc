package pl.skuteczne_inwestowanie.riskcalc;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import pl.skuteczne_inwestowanie.riskcalc.exceptions.NoFoundCurrencyException;

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

        public void setName(String name) {
            this.name = name;
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

        setQuotation("USDPLN", 3.34050);
        setQuotation("RUBPLN", 0.07059);
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


            result = Double.parseDouble(tokens[6]);

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public double getQuotation(String what) throws NoFoundCurrencyException {
        boolean thereIs = false;
        double result = 0;
        for (int i = 0; i < listOfCurrencies.size(); i++) {
            if (listOfCurrencies.get(i).getName().equalsIgnoreCase(what)) {
                thereIs = true;
                result = listOfCurrencies.get(i).getQuotation();
            }
        }
        //if (!thereIs) throw new NoFoundCurrencyException();
        if (!thereIs) {
            //probably not updated yet
            if (!(what.substring(0,2).equalsIgnoreCase(what.substring(3,5)))) new UpdateTask().execute(what);
            return 1.0;
        }

        return result;
    }

    public void setQuotation(String what,double howMuch) {
        boolean thereIs = false;
        for (int i = 0; i < listOfCurrencies.size(); i++) {
            if (listOfCurrencies.get(i).getName().equalsIgnoreCase(what)) {
                thereIs = true;
                listOfCurrencies.get(i).setQuotation(howMuch);
            }
        }
        if (!thereIs) listOfCurrencies.add(new DownloadedCurrency(what, howMuch));
    }

    public void setQuotation(DownloadedCurrency dc) {
        setQuotation(dc.getName(),dc.getQuotation());
    }

    private class UpdateTask extends AsyncTask<String, Void, DownloadedCurrency> {

        @Override
        protected DownloadedCurrency doInBackground(String... params) {
            //if base and quoted currency are equal there are not in stooq.pl
            if (params[0].substring(0,2)==params[0].substring(3,5))
                return new DownloadedCurrency(params[0], 1.0);
            else return new DownloadedCurrency(params[0],downloadQuotation(params[0]));
        }

        @Override
        protected void onPostExecute(DownloadedCurrency downloadedCurrency) {
            setQuotation(downloadedCurrency);
        }
    }

    public void updateET(MainActivity ma, EditText et, Instrument ins) throws NoFoundCurrencyException {
        UpdateTask updateTask = new UpdateTask();
        updateTask.execute(ins.getBaseCurrency()+ins.getQuotedCurrency());
        ma.setEtValue(et, getQuotation(ins.getBaseCurrency()+ins.getQuotedCurrency()), -(int) Math.log10(ins.getTickSize()));
        ma.rememberOpenPrice();
    }
}
