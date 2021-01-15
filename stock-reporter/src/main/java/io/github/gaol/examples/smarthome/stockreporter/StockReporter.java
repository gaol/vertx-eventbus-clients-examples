package io.github.gaol.examples.smarthome.stockreporter;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.eventbus.client.EventBusClient;
import io.vertx.ext.eventbus.client.EventBusClientOptions;
import io.vertx.ext.eventbus.client.MessageConsumer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This APP tries to retrieve latest specific stock price from public API then publish to a Vert.x EventBus address.
 */
public class StockReporter {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8880;
        long interval = args.length > 2 ? Long.parseLong(args[2]) : 5000;
        interval *= 20L; // default to 1 minute
        System.out.println(String.format("Trying to connect sockjs bridge at: %s:%d/, sending stock prices on each %d milliseconds ", host, port, interval));
        StockReporter app = new StockReporter(host, port, interval);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::close));
    }

    private static final String RSS_STOCK = "smart.home.rss.stock";
    private static final String RSS_ASK_STOCK = "smart.home.rss.ask.stock";

    private final EventBusClient client;
    private final long interval;
    private final StockPriceFetcher stockPriceFetcher;
    private final MessageConsumer<String> consumer;
    private final Timer timer;

    private StockReporter(String host, int port, long interval) {
        this.interval = interval;
        EventBusClientOptions options = new EventBusClientOptions()
                .setHost(host)
                .setPort(port)
                .setWebSocketPath("/eventbus")
                ;
        client = EventBusClient.webSocket(options);

        this.stockPriceFetcher = new StockPriceFetcher();
        // set up a reply address
        consumer = client.consumer(RSS_ASK_STOCK, msg -> {
            String stock = msg.headers().getOrDefault("stock", "sz000725");
            stockPriceFetcher.stockPrice(stock)
                    .onSuccess(price -> msg.reply(new JsonObject().put("status", "OK").put("price", price)))
                    .onFailure(e -> msg.reply(new JsonObject().put("status", "Err").put("error", e.getMessage())));
        });

        this.timer = new Timer();
    }

    private void start() {
        client.connect();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                publishJoke();
            }
        }, interval, interval);
    }

    private void publishJoke() {
        stockPriceFetcher.stockPrice("")
                .onSuccess(price -> client.publish(RSS_STOCK, new JsonObject().put("status", "OK").put("price", price)))
                .onFailure(Throwable::printStackTrace);
    }

    private void close() {
        timer.cancel();
        consumer.unregister();
        client.close();
        stockPriceFetcher.close();
    }

}
