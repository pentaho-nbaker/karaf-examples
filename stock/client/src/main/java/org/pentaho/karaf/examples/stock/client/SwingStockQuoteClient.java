package org.pentaho.karaf.examples.stock.client;

import org.pentaho.karaf.examples.stock.IStockMarket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by nbaker on 12/16/16.
 */
public class SwingStockQuoteClient extends JFrame {

    IStockMarket stockMarket;

    public SwingStockQuoteClient(IStockMarket market ) {
        super("Default Title" );
        this.stockMarket = market;
    }

    @Override
    protected JRootPane createRootPane() {
        JRootPane rootPane = super.createRootPane();

        //Controls to be added to the HBox
        JLabel label = new JLabel("Stock Symbol:");
        final JLabel result = new JLabel();
        result.setAlignmentX( Component.CENTER_ALIGNMENT );
        final JTextField tb = new JTextField();
        tb.setPreferredSize(new Dimension(160, tb.getPreferredSize().height));

        JButton button = new JButton("Get Price");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String symb = tb.getText();
                Double stockPrice = stockMarket.getStockPrice(symb);
                result.setText(symb + " : " + NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                        .format(stockPrice));
            }
        });

        Box vbox = Box.createVerticalBox( );
        Box hbox = Box.createHorizontalBox();
        hbox.add( label );
        hbox.add( Box.createHorizontalStrut(8));
        hbox.add( tb );
        hbox.add( Box.createHorizontalStrut(8));
        hbox.add( button );
        vbox.add( Box.createVerticalGlue() );
        vbox.add( hbox );
        hbox.add( Box.createVerticalStrut(20));
        vbox.add( result );
        vbox.add( Box.createVerticalGlue() );
        rootPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        rootPane.add( vbox );
        return rootPane;
    }

    public void init() {
        this.setSize( 400, 200 );
        this.setVisible( true );

    }
}