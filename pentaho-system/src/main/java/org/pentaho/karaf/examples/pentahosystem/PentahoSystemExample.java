package org.pentaho.karaf.examples.pentahosystem;

import org.pentaho.osgi.api.ProxyUnwrapper;
import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.action.mondrian.catalog.IMondrianCatalogService;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by nbaker on 12/27/16.
 */
public class PentahoSystemExample {

  private IUnifiedRepository        iUnifiedRepository;
  private IUserRoleListService      userRoleListService;
  private IMondrianCatalogService   catalogService;
  private ProxyUnwrapper            proxyUnwrapper;

  public PentahoSystemExample() {

    /**
     * Registered from Spring outside of OSGI. Bean wasn't published. This is the only way to get this kind of object
     *
     * @see https://github.com/pentaho/pentaho-platform/blob/7.0.0.1-R/assembly/package-res/biserver/pentaho-solutions/system/pentahoObjects.spring.xml#L127
     * @see https://github.com/pentaho/pentaho-platform/blob/7.0.0.1-R/assembly/package-res/biserver/pentaho-solutions/system/repository.spring.xml#L179
     */
    iUnifiedRepository = PentahoSystem.get( IUnifiedRepository.class );

    /**
     * Registered from OSGI, flowing through PentahoSystem and back here. This could be replaced with an OSGI service
     * reference.
     *
     * @see https://github.com/pentaho/pentaho-osgi-bundles/blob/7.0.0.1-R/pentaho-osgi-utils-impl/src/main/java/org/pentaho/osgi/impl/BeanFactoryActivator.java#L46
     */
    proxyUnwrapper = PentahoSystem.get( ProxyUnwrapper.class );
  }


  /**
   * The MondrianCatalog is a Spring bean not published. Only available thru PentahoSystem.get
   *
   * @param catalogService
   */
  public void setMondrianCatalog( IMondrianCatalogService catalogService ) {
    this.catalogService = catalogService;
  }

  /**
   * UserRoleList is a published bean from Spring and is available via OSGI Service Registry.
   *
   * @param userRoleListService
   */
  public void setUserRoleListService( IUserRoleListService userRoleListService ) {
    this.userRoleListService = userRoleListService;
  }

  public void init() {

    // Print all fields ( [ name ]: [ value of class name ] )
    String output = "PentahoSystemExample received the following implementations:\n\n" +
      Arrays.stream( this.getClass().getDeclaredFields() )
        .collect( Collectors.toMap( Field::getName, f -> getClassName(f, PentahoSystemExample.this ) ) ).entrySet().stream()
        .map( entry -> String.format( "\t%1$-25s%2$s\n", entry.getKey(), entry.getValue() ) ).collect(
        Collectors.joining() );

    LoggerFactory.getLogger( getClass() ).error( output );
  }

  private static String getClassName( Field f, Object o ) {
    try {
      return f.get( o ).getClass().getName();
    } catch ( IllegalAccessException e ) {
      return "";
    }
  }

  private String printClass( String name, Object obj ) {
    return String.format( "\t%1$-25s%2$s\n", name, obj.getClass().getName() );
  }

}
