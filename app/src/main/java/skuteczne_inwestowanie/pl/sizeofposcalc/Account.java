package skuteczne_inwestowanie.pl.sizeofposcalc;

/**
 * Created by trevny on 2014-11-10.
 */
public class Account {
    private String currency;
    private double balance;
    private double maxRisk; //in percents

    public this()    {
        //temporary
        currency = "PLN";
        balance = 10000;
        maxRisk = 0.02;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMaxRisk() {
        return maxRisk;
    }

    public void setMaxRisk(double maxRisk) {
        this.maxRisk = maxRisk;
    }
}
