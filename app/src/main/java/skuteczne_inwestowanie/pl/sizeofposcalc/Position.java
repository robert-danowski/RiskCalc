package skuteczne_inwestowanie.pl.sizeofposcalc;

/**
 * Created by teodor on 2014-11-10.
 * link between market and our account, we can choose moment and size of our position
 * we take care of proper number of decimal places in this class not in activity
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

        openPrice = 1.2486;
        sl = openPrice - 20 * instrument.getTickSize();
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
