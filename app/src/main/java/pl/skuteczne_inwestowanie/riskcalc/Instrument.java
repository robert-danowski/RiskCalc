package pl.skuteczne_inwestowanie.riskcalc;

import java.io.Serializable;

/**
 * Created by trevny on 2014-11-10.
 */
public class Instrument implements Serializable {
    private String baseCurrency; //for stocks name of stock
    private String quotedCurrency;
    private double tickSize; //0.0001 for EURUSD
    private double tickValue; //in quotedCurrency (for EURUSD in USD)
    private double minPos; //the smallest position in lots (in admiral markets it is 0.01 lots for eurusd)

    public Instrument(String bC, String qC, double tS, double tV, double mP) {
        baseCurrency = bC;
        quotedCurrency = qC;
        tickSize = tS;
        tickValue = tV;
        minPos = mP;
    }

    public Instrument()    {
        baseCurrency = "EUR";
        quotedCurrency = "USD";
        tickSize = 0.0001;
        tickValue = 10;
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

    public double getTickSize() {
        return tickSize;
    }

    public void setTickSize(double tickSize) {
        this.tickSize = tickSize;
    }

    public double getTickValue() {
        return tickValue;
    }

    public void setTickValue(double tickValue) {
        this.tickValue = tickValue;
    }

    public double getMinPos() {
        return minPos;
    }

    public void setMinPos(double minPos) {
        this.minPos = minPos;
    }
}
