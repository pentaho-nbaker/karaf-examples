# Karaf Examples - Stock Quotes
## Introduction
This Karaf Example illustrates common architectural patterns and technologies
in use at Pentaho
* __API/Impl split__ - Clients depend on API only, Implementations are managed by OSGI
* __Blueprint__ - Bundles wired-up with Blueprint Beans. No OSGI APIs in project code
* __Configuration__ - Using the Configuration Admin of OSGI

## What does it do?
When activated a Swing window is opened which allows you to retrive the current asking 
price for any stock symbol.

## Basic Architecture ##
The Client UI is given a reference to the StockMarket implementation of the IStockMarket Interface. 
This implementation calls out to Yahoo Finance to retrieve the quote.

Components are instantiated and wired together using Blueprint XML files.

## Modules
* __api__ : Java Interfaces for the Stock Market
* __impl__ : Implementations. Currently only one based on Yahoo Finance REST call
* __client__ : Swing-based UI working with the Stock Market
* __tests__ : PAX-Exam tests feature in real Karaf from within JUnit
* __feature__ : Stock application can be deployed into any Karaf container easily
* __assembly__ : Create Karaf assembly preconfigured with Stock application


## Building the Example
### Prerequisites
1. Maven must be installed and available to the command-line interface

__Build with Maven__
1. Open a command-line and navigate to this directory on your filesystem
2. Execute `mvn install`

## Running the example

### Install Feature manually into Karaf

Now that you've successfully built and installed the artifacts to your local Maven repository 
the Sock feature can be deployed into any Karaf container.

### Start Karaf
1. Download latest version of Karaf 4.x: http://karaf.apache.org/download.html
2. Open a command-line and navigate to the Karaf directory.
3. Start Karaf and enter the Shell by executing "bin/karaf"

### Install Feature

Execute the following commands
1. `feature:repo-add mvn:pentaho.karaf.examples.stock/stock-feature/1.0-SNAPSHOT/xml/features`
2. `feature:install stock-feature`

### Run the Assembly
1. From the command-line navigate to stock/assembly/target/assembly
2. Execute `./bin/karaf`


## Important Files
* __api/src/main/java/org/pentaho/karaf/examples/stock/IStockMarket.java__ : 
API Interface to the StockMarket
* __impl/src/main/java/org/pentaho/karaf/examples/stock/impl/StockMarket.java__ : 
Yahoo Implementation of IStockMarket
* __impl/src/main/resources/OSGI-INF/blueprint/beans.xml__ : 
Blueprint XML file where implementation is registered
* __client/src/main/resources/OSGI-INF/blueprint/beans.xml__ : 
Blueprint XML file where UI is constructed and wired to the Yahoo implementation
* __feature/pom.xml__ : 
Karaf Feature xml built by karaf-maven-plugin
* __assembly/pom.xml__ : 
Karaf Assembly built by karaf-maven-plugin


## Developing and Debugging
If you're running from the generated assembly you'll need to delete the packaged 
artifacts within the karaf/system directory. This way Karaf will load these bundles 
from your local Maven cache. If this is not done your 
local development changes will never go into effect as the packaged versions 
will be used.

### Delete the Packaged Bundles
1. Delete the $KARAF_HOME/system/pentaho directory

### Standard Development Flow
1. Start Karaf in debug mode: `./bin/karaf debug`
2. Attach your IDE to the standard 5005 debug port.
3. Make changes to a bundle
4. `mvn install` the affected bundle

*"update" the changed bundle from within Karaf console:*
5. This involves first finding the Bundle ID of your bundle by executing `list`
6. Update your changed bundle via `update XX`

### Bundle:Watch Flow
Karaf can automate the process of updating bundles. Before making your changes, 
setup Karaf to "watch" the bundles you're working with. Karaf will automatically 
"udpate" your bundles whenever you "mvn install" them.
1. After starting Karaf identify the Bundle IDs with `list`
2. Execute `bundle:watch XX` for each of the bundles you're working on
3. Continue to develop and install your bundles into Maven

## Make some changes
### Implement Error Handling

You'll notice that the StockMarket class contains no error handling. 
1. Extend the 
API with a custom Exception
2. Modify the getQuote() method in the IStockMarket interface to throw this new
Exception
3. Implement error handling in StockMarket implementation and the SwingStockQuoteClient UI

### Sweeten the UI

The UI certainly could be better, make some adjustments.
