package org.pentaho.karaf.examples.stock.client;

import org.pentaho.karaf.examples.stock.IStockMarket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This simple UI is given references to any IStockMarket implementations registered in OSGI, and is notified when
 * impls come and go over the course of execution.
 * <p>
 * The UI itself is simple, textbox for the stock symbol, combobox to select the impl, and a button.
 * <p>
 * Created by nbaker on 12/16/16.
 */
public class SwingStockQuoteClient extends JFrame {

  List<IStockMarket> stockMarkets;
  private String defaultMarket;
  private JComboBox<IStockMarket> marketJComboBox;

  /**
   * The List of Markets is a proxy managed by Blueprint. It will grow and shrink as implementations come and go
   * within OSGI
   *
   * @param markets
   */
  public SwingStockQuoteClient( List<IStockMarket> markets, String defaultMarket ) {
    super( "Default Title" );
    this.stockMarkets = markets;
    this.defaultMarket = defaultMarket;

    createUI();
  }

  /**
   * Blueprint calls this method after constructing the class instance
   */
  public void init() {
    this.setSize( 500, 200 );
    this.setVisible( true );
  }

  /**
   * Called by blueprint when bundle is stopped/uninstalled
   */
  public void destroy() {
    this.setVisible( false );
  }

  public void marketAdded( IStockMarket market ) {
    updateMarketUI();
  }

  public void marketRemoved( IStockMarket market ) {
    updateMarketUI();
  }


  private void createUI() {
    final JLabel label = new JLabel( "Stock Symbol:" );
    final JLabel result = new JLabel();
    final JTextField tb = new JTextField();
    marketJComboBox = new JComboBox<>();
    final JButton button = new JButton( "Get Price" );

    result.setAlignmentX( Component.CENTER_ALIGNMENT );

    tb.setPreferredSize( new Dimension( 160, tb.getPreferredSize().height ) );
    marketJComboBox.setPreferredSize( new Dimension( 100, marketJComboBox.getPreferredSize().height ) );

    button.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        String symb = tb.getText();
        IStockMarket stockMarket = stockMarkets.get( marketJComboBox.getSelectedIndex() );
        Double stockPrice = stockMarket.getStockPrice( symb );
        result.setText( symb + " : " + NumberFormat.getCurrencyInstance( new Locale( "en", "US" ) )
          .format( stockPrice ) );
      }
    } );

    Box vbox = Box.createVerticalBox();
    Box hbox = Box.createHorizontalBox();
    hbox.add( label );
    hbox.add( Box.createHorizontalStrut( 8 ) );
    hbox.add( tb );
    hbox.add( Box.createHorizontalStrut( 8 ) );
    hbox.add( marketJComboBox );
    hbox.add( Box.createHorizontalStrut( 8 ) );
    hbox.add( button );
    vbox.add( Box.createVerticalGlue() );
    vbox.add( hbox );
    hbox.add( Box.createVerticalStrut( 20 ) );
    vbox.add( result );
    vbox.add( Box.createVerticalGlue() );
    getContentPane().setLayout( new FlowLayout( FlowLayout.CENTER, 20, 20 ) );
    getContentPane().add( vbox );

    updateMarketUI();
  }

  private void updateMarketUI() {

    // Blueprint events are likely not on the event thread, so first thing to do is switch there
    SwingUtilities.invokeLater( () -> {
      DefaultComboBoxModel model = (DefaultComboBoxModel) marketJComboBox.getModel();
      model.removeAllElements();
      stockMarkets.stream().map( Object::toString ).forEach( model::addElement );
      Optional<IStockMarket> selectedMarket =
        stockMarkets.stream().filter( m -> m.toString().equals( defaultMarket ) ).findFirst();
      selectedMarket.ifPresent( iStockMarket -> marketJComboBox.setSelectedItem( defaultMarket ) );
    } );
  }

}