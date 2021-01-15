# A SmartHome application demonstrates Vert.x EventBus Bridge Clients

This is a demo application designed to show how Vert.x EventBus Bridge Clients are used.

## Modules

There are several modules in the application:
* TCP Bridge        -    An EventBus TCP Bridge listen for clients.
* Joke Teller       -    A Java based EventBus TCP Bridge Client to tell jokes.


## Build the application

> mvn clean install


## Start the TCP bridge

In `tcp-bridge` folder, run:

> mvn clean install exec:java

to start the tcp bridge on port `7000`, or

> mvn clean install exec:java -Dsmarthome.tcpbridge.port=8888

to specify the tcp bridge port

## Start the Joke Teller client

In `joke-teller` folder, run:

> mvn clean install exec:java
