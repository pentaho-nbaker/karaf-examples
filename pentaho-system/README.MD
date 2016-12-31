# Karaf Examples - Pentaho System
## Introduction
This Karaf Example shows how Pentaho has integrated OSGI into the legacy PentahoSystem. 

Travis CI Build: https://travis-ci.org/pentaho-nbaker/karaf-examples

## What does it do?
This pentaho-system bundle has a single class PentahoSystemExample which is constructed via Blueprint XML. 
Two of it's properties are injected by the Blueprint container, two others are found programmatically
in the classes constructor.

On initialization this class prints out it's fields and values to the logger.


---

## Important Files
* [PentahoSystemExample.java](src/main/java/org/pentaho/karaf/examples/pentahosystem/PentahoSystemExample.java) : API Interface to the StockMarket
* [Blueprint XML](src/main/resources/OSGI-INF/blueprint/beans.xml) 

---

## Building the Example
### Prerequisites
1. Maven must be installed and available to the command-line interface

__Build with Maven__

1. Open a command-line and navigate to this directory on your filesystem
2. Execute `mvn install`

---

## Running the example
This bundle must be deployed into a Pentaho Server as it has dependencies on the Platform.

### Install Bundle manually into the Pentaho Server
From the command-line execute the following. Note your port may be higher if other server are running.
Check your server logs if the connection fails.
```
ssh admin@localhost -p 8802
install mvn:pentaho.karaf.examples/pentaho-system/1.0-SNAPSHOT
start pentaho-system
```

You should see output in the tomcat/logs/catalina.out like the following:
```
19:54:36,234 ERROR [PentahoSystemExample] PentahoSystemExample received the following implementations:

	userRoleListService      Proxy7c22a081_d7c7_4de3_b7ee_bf85c6eca6e0
	catalogService           org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalogHelper
	proxyUnwrapper           null
	iUnifiedRepository       org.pentaho.platform.repository2.unified.ExceptionLoggingDecorator
```

Here the PentahoSystemExample bean has been constructed and all of it's Fileds are printed to the log


## Developing and Debugging
Since this is a new bundle there's no need to clear the it from Karaf Maven Repository. The easiest route
is to employ the Karaf watch command. (You'll notice that we're using the bundle symbolic name instead of 
the bundle ID. Either will work)
1. From the SSH console execute `bundle:watch pentaho-system`

---

## Make some changes
### Switch ProxyUnwrapper to OSGI Reference

The example looks up ProxyUnwrapper from PentahoSystem. This can be looked up via the OSGI Service 
Registry instead. 
1. Modify the class to add a setter for the proxyUnwrapper field.
2. Remove the lookup in the constructor
3. Modify the Blueprint file to inject a <reference> to the ProxyUnwrapper implementation.
4. Rebuild the bundle: `mvn install`

You should notice the Bundle init message is printed again and this time the proxyUnwrapper's Class Name
has switched from "org.pentaho.osgi.impl.ProxyUnwrapperImpl" to something like 
"Proxyf798161a_8263_4e9b_9f62_6750957e4cf6". This is because Blueprint doesn't inject 
the raw object registered in the system. Rather it injects a Proxy object which delegates to it. If this
implementation were to be unregistered in the system the Proxy would reacquire a new instance and block all
calls until then. This behavior is known as "dampening". 

### Harden the debug message
There's an NPE waiting to happen in the out-of-the-box implementation of PentahoSystemExample. If a field's 
value is null a NPE will be produced.

1. Comment-out one of the PentahoSystem lookups in the constructor and redeploy the bundle `mvn install`
2. Notice that the pentahoSystemExample Bean failed to initialize due to the NPE
3. Harden the getObjectName() method to return the string "null" if the field is empty
4. Redeploy the bundle
4. Make sure the Bean initialized and "null" was printed for that field