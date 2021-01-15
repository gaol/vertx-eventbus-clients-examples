package io.github.gaol.examples.smarthome.tcpbridge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;

public class TCPBridgeVerticle extends AbstractVerticle {

    private static final String RSS_JOKE = "smart.home.rss.joke";
    private static final String RSS_ASK_JOKE = "smart.home.rss.ask.joke";

    private TcpEventBusBridge tcpBridge;
    private MessageConsumer<JsonObject> jokeConsumer;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex(".*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
        tcpBridge = TcpEventBusBridge.create(vertx, options);
        String localHost = System.getProperty("smarthome.tcpbridge.host", "localhost");
        int port = Integer.getInteger("smarthome.tcpbridge.port", 7000);
        tcpBridge.listen(port, localHost, f -> {
            if (f.succeeded()) {
                System.out.println("TCP Bridge is up listening on port: " + port);
                startPromise.complete();
            } else {
                startPromise.fail(f.cause());
            }
        });

        EventBus eventBus = vertx.eventBus();
        jokeConsumer = eventBus.consumer(RSS_JOKE, h -> System.out.println("Got a Joke: " + h.body()));

    }

    @Override
    public void stop() throws Exception {
        if (this.tcpBridge != null) {
            tcpBridge.close();
        }
        if (this.jokeConsumer != null) {
            jokeConsumer.unregister();
        }
    }
}
