package org.pentaho.karaf.examples.stock;

/**
 * Created by nbaker on 12/16/16.
 */
public interface IStockMarket {

    /**
     * Get the current asking price for the given stock
     *
     * @param symbl
     * @return current asking price of stock
     */
    Double getStockPrice( String symbl );
}
