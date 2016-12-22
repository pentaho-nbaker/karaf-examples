# Karaf Examples - Stock Quotes
## Introduction
This Karaf Example illustrates common architectural patterns and technologies
in use at Pentaho
* __API/Impl split__ - Clients depend on API only, Implementations are managed by OSGI
* __Blueprint__ - Bundles wired-up with Blueprint Beans, our IOC container of choice. Usage of Blueprint keeps OSGI APIs away from project code. IOC managed code is much easier to test.
* __Configuration__ - Using the Configuration Admin of OSGI. Configurations are injected into the project classes via the Blueprint IOC. Multiple options exist and are highlighted

Travis CI Build: https://travis-ci.org/pentaho-nbaker/karaf-examples

## What does it do?
When activated a Swing window is shown which allows the retrival of the current asking 
price for any stock symbol.

## Basic Architecture ##
The Client UI is instantiated by Blueprint where it is also given reference to any IStockMarket implementations registered from "impl" bundle. There are currently two implementations: Yahoo and Google

---

## Modules
* __api__ : Java Interfaces for the Stock Market
* __impl__ : Implementations. Currently two: Yahoo and Google
* __client__ : Swing-based UI working with the Stock Markets
* __tests__ : Bundles are tested within a real Karaf container via PAX-Exam
* __feature__ : Produces a Karaf Feature which allow the application to be deployed in any Karaf container
* __assembly__ : Produces a Karaf assembly preconfigured with Stock application


## Important Files
* [IStockMarket.java](api/src/main/java/org/pentaho/karaf/examples/stock/IStockMarket.java) : API Interface to the StockMarket
* [YahooStockMarket](impl/yahoo/src/main/java/org/pentaho/karaf/examples/stock/yahoo/YahooStockMarket.java) : Yahoo Implementation of IStockMarket
* [GoogleStockMarket](impl/google/src/main/java/org/pentaho/karaf/examples/stock/google/GoogleStockMarket.java) : Google Implementation of IStockMarket
* [Yahoo Blueprint](impl/yahoo/src/main/resources/OSGI-INF/blueprint/beans.xml) / [Google Blueprint](impl/google/src/main/resources/OSGI-INF/blueprint/beans.xml) : Blueprint XML filea where implementations are registered
* [Client Blueprint](client/src/main/resources/OSGI-INF/blueprint/beans.xml) : Blueprint XML file where UI is constructed and wired to the implementations
* [Feature Build](feature/pom.xml) : Karaf Feature xml built by karaf-maven-plugin
* [Assembly Build](assembly/pom.xml) : Karaf Assembly built by karaf-maven-plugin

---

## Building the Example
### Prerequisites
1. Maven must be installed and available to the command-line interface

__Build with Maven__

1. Open a command-line and navigate to this directory on your filesystem
2. Execute `mvn install`

---

## Running the example

### Install Feature manually into Karaf

Now that you've successfully built and installed the artifacts to your local Maven repository 
the Sock feature can be deployed into any Karaf container.

### Start Karaf
1. Download latest version of Karaf 4.x: http://karaf.apache.org/download.html
2. Open a command-line and navigate to the Karaf directory.
3. Start Karaf and enter the Shell by executing "bin/karaf"

### Install Feature
Execute the following in the Karaf Shell
```
feature:repo-add mvn:pentaho.karaf.examples.stock/stock-feature/1.0-SNAPSHOT/xml/features
feature:install stock-feature
```

### Run the Assembly
From the command-line navigate to stock/assembly/target/assembly. Execute the following:
```
./bin/karaf
```

---

## Developing and Debugging
If you're running from the generated assembly, delete the packaged 
artifacts within the karaf/system directory. This will ensure Karaf loads these bundles 
from your local Maven cache. If this is not done your 
local development changes will never go into effect as the packaged versions 
will always be used.

### Delete the Packaged Bundles
1. Delete the $KARAF_HOME/system/pentaho directory

### Standard Development Flow
1. Start Karaf in debug mode: `./bin/karaf debug`
2. Attach your IDE to the standard 5005 debug port.
3. Make changes to a bundle
4. `mvn install` the affected bundle

At this point you can shutdown and restart Karaf and your changes will be loaded on next start. However, it's far nicer to take advantage of the ability of OSGI to reload bundles without restart. You also never have to restart your debugging session!

5. Switch to the Karaf Shell
6. Find the ID of the bundle you're working with by executing `list`
7. Update your changed bundle with the `update XX` command

### Bundle:Watch Flow
Karaf can automate the process of updating bundles. Before making your changes, 
setup Karaf to "watch" the bundles you're working on. Karaf will automatically 
"udpate" your bundles whenever you "mvn install" them.

1. After starting Karaf identify the Bundle IDs with `list`
2. Execute `bundle:watch XX` for each of the bundles you're working on
3. Continue to develop and install your bundles into Maven

---

## Make some changes
### Implement Error Handling

You'll notice that the StockMarket class contains no error handling. 

1. Extend the API with a custom Exception Class
2. Modify the getQuote() method in the IStockMarket interface to throw this new Exception
3. Implement error handling in StockMarket implementation and the SwingStockQuoteClient UI

### Sweeten the UI

The UI certainly could be better, make some adjustments.
