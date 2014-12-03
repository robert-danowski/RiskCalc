package pl.skuteczne_inwestowanie.riskcalc;

import java.io.Serializable;

/**
 * Created by trevny on 2014-11-10.
 */
public class Account implements Serializable {
    private String currency;
    private double balance;
    private double maxRisk; //in percents
    private double minUnit; //for most currencies it is 0.01

    public Account(String c, double b, double mR, double mU) {
        currency = c;
        balance = b;
        maxRisk = mR;
        minUnit = mU;
    }

    public Account() {
        this("PLN", 10000, 0.02, 0.01);
    }

    public Account(Account account) {
        currency = account.getCurrency();
        balance = account.getBalance();
        maxRisk = account.getMaxRisk();
        minUnit = account.getMinUnit();
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

    public double getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(double minUnit) {
        this.minUnit = minUnit;
    }
}
