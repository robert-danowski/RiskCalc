package pl.skuteczne_inwestowanie.riskcalc;

import pl.skuteczne_inwestowanie.riskcalc.exceptions.NoFoundCurrencyException;

/**
 * Created by teodor on 2014-11-10.
 */
public class ConvertCurrency {
    public static double calc(double inValue,String inCur,String outCur) throws NoFoundCurrencyException {
        QuotationDownloader qd = new QuotationDownloader();
        return inValue * qd.getQuotation(inCur + outCur);
    }
}
