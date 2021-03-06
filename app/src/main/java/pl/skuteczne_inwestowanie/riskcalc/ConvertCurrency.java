package pl.skuteczne_inwestowanie.riskcalc;

import pl.skuteczne_inwestowanie.riskcalc.exceptions.NoFoundCurrencyException;

/**
 * Created by teodor on 2014-11-10.
 */
public class ConvertCurrency {
    public static double calc(double inValue,String inCur,String outCur) throws NoFoundCurrencyException {
        QuotationDownloader qd = new QuotationDownloader();
        if (inCur.equalsIgnoreCase(outCur)) return 1.0;
        else return inValue * qd.getQuotation(inCur + outCur);
    }
}
