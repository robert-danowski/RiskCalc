package skuteczne_inwestowanie.pl.sizeofposcalc;

/**
 * Created by teodor on 2014-11-10.
 * link between market and our account, we can choose moment and size of our position
 */
public class Position {
    private Instrument instrument;
    private Account account;
    private double sl; //stop loss
    private double size; //size in lots

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

    public void calcSize() {
        double MaxCapitalAtRisk=account.getMaxRisk()*account.getBalance();
        double oneLotRisk=Math.abs(instrument.getPrice()-sl)/pointSize
        size = MaxCapitalAtRisk/oneLotRisk;
    }
}
