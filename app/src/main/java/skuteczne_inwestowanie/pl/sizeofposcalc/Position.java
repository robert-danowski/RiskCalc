package skuteczne_inwestowanie.pl.sizeofposcalc;

/**
 * Created by teodor on 2014-11-10.
 * link between market and our account, we can choose moment and size of our position
 */
public class Position {
    private Instrument instrument;
    private Account account;
    private double openPrice; //open price
    private double sl; //stop loss
    private double size; //size in lots

    //temporary
    public Position() {
        instrument = new Instrument();
        account = new Account();

        openPrice = instrument.getPrice();
        sl = openPrice - 20 * instrument.getPointSize();
        size = instrument.getMinPos();
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

    public double calcSlOffset() {
        return Math.floor((openPrice - sl) / instrument.getPointSize());
    }

    public double calcSize() {
        double MaxCapitalAtRisk = account.getMaxRisk() * account.getBalance();
//        double oneLotRisk = Math.floor(
//                calcSlOffset()
//                        * ConvertCurrency.calc(
//                        instrument.getLotValue(), instrument.getQuotedCurrency(), account.getCurrency()
//                ) / instrument.getMinPos()) * instrument.getMinPos();
        int tempCalc = (int) Math.floor(calcSlOffset()
                * ConvertCurrency.calc(instrument.getLotValue(), instrument.getQuotedCurrency(), account.getCurrency())
                / instrument.getMinPos());
        double oneLotRisk=tempCalc* instrument.getMinPos();
        size = MaxCapitalAtRisk / oneLotRisk;
        return size;
    }
}
