package io.github.gaol.examples.smarthome.joketeller;

import io.vertx.ext.eventbus.client.EventBusClient;
import io.vertx.ext.eventbus.client.EventBusClientOptions;
import io.vertx.ext.eventbus.client.MessageConsumer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This APP tries to retrieve random jokes from public API then publish to a Vert.x EventBus address.
 */
public class JokeTeller {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 7000;
        long interval = args.length > 2 ? Long.parseLong(args[2]) : 5000;
        interval *= 20L; // default to 1 minute
        System.out.println(String.format("Trying to connect tcp bridge at: %s:%d, sending random jokes on each %d milliseconds ", host, port, interval));
        JokeTeller app = new JokeTeller(host, port, interval);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::close));
    }

    private static final String RSS_JOKE = "smart.home.rss.joke";
    private static final String RSS_ASK_JOKE = "smart.home.rss.ask.joke";

    private final EventBusClient client;
    private final long interval;
    private final JokeFetcher jokeFetcher;
    private final MessageConsumer<String> consumer;
    private final Timer timer;

    private JokeTeller(String host, int port, long interval) {
        this.interval = interval;
        EventBusClientOptions options = new EventBusClientOptions()
                .setHost(host)
                .setPort(port);
        client = EventBusClient.tcp(options);

        this.jokeFetcher = new JokeFetcher();
        // set up a reply address
        consumer = client.consumer(RSS_ASK_JOKE, msg -> msg.reply(jokeFetcher.randomJoke()));
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
        client.publish(RSS_JOKE, jokeFetcher.randomJoke());
    }

    private void close() {
        timer.cancel();
        consumer.unregister();
        client.close();
    }

}
