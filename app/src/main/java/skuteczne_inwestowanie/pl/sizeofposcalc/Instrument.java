package skuteczne_inwestowanie.pl.sizeofposcalc;

/**
 * Created by trevny on 2014-11-10.
 */
public class Instrument {
    private String baseCurrency; //for stocks name of stock
    private String quotedCurrency;
    private double price; //how much quoted Currency you should give for 1 base currency
    private double pointSize; //0.0001 for EURUSD

    public this() {
        baseCurrency="EUR"
        quotedCurrency="USD";
        price=1.2486;
        pointSize=0.0001;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuotedCurrency() {
        return quotedCurrency;
    }

    public void setQuotedCurrency(String quotedCurrency) {
        this.quotedCurrency = quotedCurrency;
    }

    public double getPointSize() {
        return pointSize;
    }

    public void setPointSize(double pointSize) {
        this.pointSize = pointSize;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
