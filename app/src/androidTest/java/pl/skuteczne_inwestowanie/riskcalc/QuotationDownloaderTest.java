package pl.skuteczne_inwestowanie.riskcalc;

import junit.framework.TestCase;

import pl.skuteczne_inwestowanie.riskcalc.exceptions.CurrencyNotFoundException;

public class QuotationDownloaderTest extends TestCase {
    public void testForEurUsd() throws CurrencyNotFoundException {
        QuotationDownloader qd=new QuotationDownloader();
        double quotation=qd.getQuotation("EURUSD");
        assertTrue(quotation>0);
    }
    public void testForEurEur() throws CurrencyNotFoundException {
        QuotationDownloader qd=new QuotationDownloader();
        double quotation=qd.getQuotation("EUREUR");
        assertTrue(quotation>0);
    }
    public void testForUsdRub() throws CurrencyNotFoundException {
        QuotationDownloader qd=new QuotationDownloader();
        double quotation=qd.getQuotation("USDRUB");
        assertTrue(quotation>0);
    }
}