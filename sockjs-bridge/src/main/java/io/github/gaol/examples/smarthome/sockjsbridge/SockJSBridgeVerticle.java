package io.github.gaol.examples.smarthome.sockjsbridge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class SockJSBridgeVerticle extends AbstractVerticle {

    private static final String RSS_STOCK = "smart.home.rss.stock";
    private static final String RSS_ASK_STOCK = "smart.home.rss.ask.stock";

    private MessageConsumer<JsonObject> jokeConsumer;
    private Router router;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        String localHost = System.getProperty("smarthome.httpserver.host", "localhost");
        int port = Integer.getInteger("smarthome.httpserver.port", 8880);

        router = Router.router(vertx);
        SockJSBridgeOptions bridgeOptions = new SockJSBridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex(".*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
        router.mountSubRouter("/eventbus", SockJSHandler.create(vertx).bridge(bridgeOptions));

        HttpServerOptions httpServerOptions = new HttpServerOptions().setPort(port);
        HttpServer httpServer = vertx.createHttpServer(httpServerOptions);
        httpServer.requestHandler(router).listen(port, localHost, f -> {
            if (f.succeeded()) {
                System.out.println("TCP Bridge is up listening on port: " + httpServer.actualPort());
                startPromise.complete();
            } else {
                startPromise.fail(f.cause());
            }
        });

        EventBus eventBus = vertx.eventBus();
        jokeConsumer = eventBus.consumer(RSS_STOCK, h -> System.out.println("Got a Joke: " + h.body()));
    }

    @Override
    public void stop() throws Exception {
        if (this.jokeConsumer != null) {
            jokeConsumer.unregister();
        }
    }
}
