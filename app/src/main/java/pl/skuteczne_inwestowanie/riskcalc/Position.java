package pl.skuteczne_inwestowanie.riskcalc;

import java.io.Serializable;

import pl.skuteczne_inwestowanie.riskcalc.exceptions.CurrencyNotFoundException;

/**
 * Created by teodor on 2014-11-10.
 * link between market and our account, we can choose moment and size of our position
 * we take care of proper number of decimal places in this class not in activity
 */
public class Position implements Serializable {
    private Account account;
    private Instrument instrument;
    private double openPrice; //open price
    private double sl; //stop loss
    private double size; //size in lots

    public Position(Account acc, Instrument ins, double oP, double stopLoss, double sizePos) {
        instrument = ins;
        account = acc;

        openPrice = oP;
        sl = stopLoss;
        size = sizePos;
    }

    public Position(Account acc, Instrument ins) {this(acc,ins,1.2486, 1.2466, 0.01);}

    public Position() {
        this(new Account(), new Instrument());
    }

    public Position(Position position) {
        this(new Account(position.getAccount()),
                new Instrument(position.getInstrument()),
                position.getOpenPrice(), position.getSl(), position.getSize());
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getSl() {
        return sl;
    }

    public void setSl(double sl) {
        this.sl = sl;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getSlOffset() {
        return (int) Math.round((openPrice - sl) / instrument.getTickSize());
    }

    public double setSlOffset(int slOffset) {
        sl = openPrice - slOffset * instrument.getTickSize();
        return sl;
    }

    public double calcOneLotRisk() {
        return Math.abs(getSlOffset()) * ConvertCurrency.calc(
                instrument.getTickValue(), instrument.getQuotedCurrency(), account.getCurrency()
        );
    }

    public double calcSize() {
        double MaxCapitalAtRisk = account.getMaxRisk() * account.getBalance();
        size = Math.floor(MaxCapitalAtRisk / calcOneLotRisk() / instrument.getMinPos()) * instrument.getMinPos();
        return size;
    }

    public double calcMoneyAtRisk() {
        return size * calcOneLotRisk();
    }

    public double calcPercentRisk() {
        return calcMoneyAtRisk() / account.getBalance();
    }

    public void setAmountRisk(double amountRisk) {
        account.setMaxRisk(amountRisk / account.getBalance());

    }

}
