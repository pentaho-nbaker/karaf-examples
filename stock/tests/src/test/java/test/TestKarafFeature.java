package test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.pentaho.karaf.examples.stock.IStockMarket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created by nbaker on 12/19/16.
 */
@RunWith( PaxExam.class )
public class TestKarafFeature {

  private static Logger LOG = LoggerFactory.getLogger( TestKarafFeature.class );

  @Inject
  protected IStockMarket market;

  public static String karafVersion() {
    ConfigurationManager cm = new ConfigurationManager();
    String karafVersion = cm.getProperty( "pax.exam.karaf.version", "4.0.7" );
    return karafVersion;
  }

  @Configuration
  public Option[] config() {
    MavenArtifactUrlReference karafUrl = maven()
      .groupId( "org.apache.karaf" )
      .artifactId( "apache-karaf" )
      .versionAsInProject()
      .type( "zip" );

    MavenUrlReference karafStandardRepo = maven()
      .groupId( "org.apache.karaf.features" )
      .artifactId( "standard" )
      .version( karafVersion() )
      .classifier( "features" )
      .type( "xml" );

    MavenUrlReference ourRepo = maven()
      .groupId( "pentaho.karaf.examples.stock" )
      .artifactId( "stock-feature" )
      .versionAsInProject()
      .classifier( "features" )
      .type( "xml" );

    return new Option[] {
      karafDistributionConfiguration()
        .frameworkUrl( karafUrl )
        .unpackDirectory( new File( "target", "exam" ) )
        .useDeployFolder( false ),
      //                debugConfiguration("5006", true), // Enable Debugging and wait for attach
      keepRuntimeFolder(),
      configureConsole().ignoreLocalConsole(),
      features( karafStandardRepo, "blueprint" ),
      features( ourRepo, "stock-feature" ),
    };
  }

  @Test
  public void testGetQuote() {
    double price = market.getStockPrice( "MSFT" );
    LOG.info( "Result of call was {}", price );
    Assert.assertTrue( price > 0 );
  }

  @Test
  public void testMakeSureGUIShown() {
    Frame[] frames = JFrame.getFrames();
    assertEquals( 1, frames.length );
    assertEquals( "Default Title", frames[ 0 ].getTitle() );
  }

}
