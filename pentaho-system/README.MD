# Karaf Examples - Pentaho System
## Introduction
This Karaf Example shows how Pentaho has integrated OSGI together with the legacy PentahoSystem and PluginRegistry systems, smoothing the migration to OSGI. 

Travis CI Build: https://travis-ci.org/pentaho-nbaker/karaf-examples

## What does it do?
This pentaho-system bundle has a single class PentahoSystemExample which is constructed via Blueprint XML. 
Two of it's properties are injected by the Blueprint container, two others are found programmatically
in the constructor.

On initialization this class prints out its fields and values to the logger.

---

## Important Files
* [PentahoSystemExample.java](src/main/java/org/pentaho/karaf/examples/pentahosystem/PentahoSystemExample.java) : One and only class.
* [Blueprint XML](src/main/resources/OSGI-INF/blueprint/beans.xml) : Instantiates PentahoSystemExample, injecting some dependencies.

---

## Building the Example
### Prerequisites
1. Maven must be installed and available to the command-line interface

__Build with Maven__

1. Open a command-line and navigate to this directory on your filesystem
2. Execute `mvn install`

---

## Running the example
This bundle must be deployed into a Pentaho Server as it has dependencies on Platform components.

### Pentaho Server Karaf setup
We'll be using the Karaf Shell via SSH to install our bundle. This requires that the "ssh" feature is enabled.

1. Open `pentaho-server/pentaho-solutions/system/karaf/etc/org.apache.karaf.feature.cfg`
2. Add `ssh` to the "featuresBoot" comma-separated list of features.
3. Re/Start the Pentaho Server

### Install Bundle manually into the Pentaho Server
From the command-line execute the following. __Note your port may be higher if other servers are running.
Check your server logs if the connection fails.__
```
ssh admin@localhost -p 8802
install mvn:pentaho.karaf.examples/pentaho-system/1.0-SNAPSHOT
start pentaho-system
```

You should see output in the tomcat/logs/catalina.out like the following:
```
19:54:36,234 ERROR [PentahoSystemExample] PentahoSystemExample received the following implementations:

	userRoleListService      Proxy44b9847d_3e94_459c_ad99_2df415934586
	catalogService           org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalogHelper
	proxyUnwrapper           org.pentaho.osgi.impl.ProxyUnwrapperImpl
	iUnifiedRepository       org.pentaho.platform.repository2.unified.ExceptionLoggingDecorator
```

Here the PentahoSystemExample bean has been constructed by our bundle and all of its Fileds have been printed to the log


## Developing and Debugging
Since this is a new bundle there's no need to clear the it from Karaf Maven Repository. The easiest way to update the bundle within your running server is by employing the Karaf watch command. (You'll notice that we're using the bundle symbolic name instead of 
the bundle ID. Either will work)

1. From the SSH console execute `bundle:watch pentaho-system`

---

## Make some changes
### Switch ProxyUnwrapper to OSGI Reference

The example looks up ProxyUnwrapper from PentahoSystem. This can be looked up via the OSGI Service 
Registry instead, our preferred method whenever possible.

1. Modify the class to add a setter for the proxyUnwrapper field.
2. Remove the lookup in the constructor
3. Modify the Blueprint file to inject a <reference> to the ProxyUnwrapper implementation.
4. Rebuild the bundle: `mvn install`

You should notice the Bundle init message is printed again and this time the proxyUnwrapper's Class Name
has switched from "org.pentaho.osgi.impl.ProxyUnwrapperImpl" to something like 
"Proxyf798161a_8263_4e9b_9f62_6750957e4cf6". This is because Blueprint doesn't inject 
the raw object registered in the system. Rather it injects a Proxy object which delegates to it. 

If the bundle supplying ProxyUnwrapper were to be unregistered this Proxy would block all
calls until it's able to reaquire a new instance. This behavior is known as "damping". The timeout is configurable, but it's not possible to disable this proxying behavior of Blueprint.

Sometimes is necessary to unwrap a proxy reference to obtain the underlying object. We accomplish by calling the ProxyUnwrapper class: `proxyUnwrapper.unwrap( blueprintProxiedService)`. The irony here is we're using a proxied reference to IProxyUnwrapper to unwrap another! 

### Harden the debug message
There's an NPE waiting to happen in the out-of-the-box implementation of PentahoSystemExample. If a field's 
value is null a NPE will be produced.

1. Comment-out one of the PentahoSystem lookups in the constructor and redeploy the bundle `mvn install`
2. Notice that the pentahoSystemExample Bean failed to initialize due to the NPE
3. Harden the getObjectName() method to return the string "null" if the field is empty
4. Redeploy the bundle
4. Verify that the bundle initialized and "null" was printed for the null field in our example class
