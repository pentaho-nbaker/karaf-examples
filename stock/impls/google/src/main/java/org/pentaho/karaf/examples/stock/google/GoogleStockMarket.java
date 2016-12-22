package org.pentaho.karaf.examples.stock.google;

import org.apache.commons.io.IOUtils;
import org.pentaho.karaf.examples.stock.IStockMarket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Retrieves current asking price for stock.
 * <p>
 * Created by nbaker on 12/16/16.
 */
public class GoogleStockMarket implements IStockMarket {
  private static String NA = "N/A";

  public Double getStockPrice( String s ) {
    InputStream inputStream = null;
    try {
      URL url = new URL( "http://finance.google.com/finance/info?q=" + s );
      inputStream = url.openStream();
      String quote = IOUtils.toString( inputStream, "UTF-8" );
      quote = quote.substring( 3 ); // first two chars are //

      JsonReader rdr = Json.createReader( new ByteArrayInputStream( quote.getBytes( Charset.forName( "UTF-8" ) ) ) );
      String price = ( (JsonObject) rdr.readArray().get( 0 ) ).getString( "l" );
      return Double.parseDouble(
        price
      );

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
    return "Google";
  }
}
