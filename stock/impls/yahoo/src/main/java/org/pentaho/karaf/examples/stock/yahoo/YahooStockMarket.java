package org.pentaho.karaf.examples.stock.yahoo;

import org.apache.commons.io.IOUtils;
import org.pentaho.karaf.examples.stock.IStockMarket;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Retrieves current asking price for stock.
 *
 * @see http://www.jarloo.com/yahoo_finance/
 * <p>
 * <p>
 * Created by nbaker on 12/16/16.
 */
public class YahooStockMarket implements IStockMarket {
  private static String NA = "N/A";

  public Double getStockPrice( String s ) {
    InputStream inputStream = null;
    try {
      URL url = new URL( "http://download.finance.yahoo.com/d/quotes.csv?s=" + s + "&f=a" );
      inputStream = url.openStream();
      String quote = IOUtils.toString( inputStream, "UTF-8" );
      return Double.parseDouble( quote );

    } catch ( MalformedURLException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly( inputStream );
    }
    return 0.0;
  }

  @Override
  public String toString() {
    return "Yahoo!";
  }
}
