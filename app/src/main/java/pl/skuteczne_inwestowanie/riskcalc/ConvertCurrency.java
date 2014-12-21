package pl.skuteczne_inwestowanie.riskcalc;

/**
 * Created by teodor on 2014-11-10.
 */
public class ConvertCurrency {
    static QuotationDownloader qd =  new QuotationDownloader();

    public static QuotationDownloader getQd() {
        return qd;
    }

    public static void setQd(QuotationDownloader qd) {
        ConvertCurrency.qd = qd;
    }

    public static double calc(double inValue,String inCur,String outCur) {
        if (inCur.equalsIgnoreCase(outCur)) return 1.0;
        else return inValue * qd.getQuotation(inCur + outCur);
    }
}
