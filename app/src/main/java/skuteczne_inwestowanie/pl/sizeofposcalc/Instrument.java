package skuteczne_inwestowanie.pl.sizeofposcalc;

/**
 * Created by trevny on 2014-11-10.
 */
public class Instrument {
    private String baseCurrency; //for stocks name of stock
    private String quotedCurrency;
    private double price; //how much quoted Currency you should give for 1 base currency
    private double pointSize; //0.0001 for EURUSD
    private double lotValue; //in quotedCurrency (for EURUSD in USD)
    private double minPos; //the smallest position in lots (in admiral markets it is 0.01 lots for eurusd)

    public Instrument()    {
        baseCurrency = "EUR";
        quotedCurrency = "USD";
        price = 1.2486;
        pointSize = 0.0001;
        lotValue = 10;
        minPos=0.01;
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

    public double getLotValue() {
        return lotValue;
    }

    public void setLotValue(double lotValue) {
        this.lotValue = lotValue;
    }

    public double getMinPos() {
        return minPos;
    }

    public void setMinPos(double minPos) {
        this.minPos = minPos;
    }
}
