package io.github.gaol.examples.smarthome.stockreporter;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class StockPriceFetcher {

    private final Vertx vertx;
    private WebClient webClient;

    public StockPriceFetcher() {
        this.vertx = Vertx.vertx();
        webClient = WebClient.create(vertx, new WebClientOptions().setTrustAll(true));
    }

    Future<String> stockPrice(String stock) {
        Promise<String> pricePromise = Promise.promise();
        Future<String> future = pricePromise.future();
        String url = String.format("", stock);
        webClient.get(url).send(resp -> {
            if (resp.succeeded()) {
                HttpResponse<Buffer> response = resp.result();
                if (response.statusCode() == 200) {
                    String body = response.bodyAsString();
                    String price = parsePrice(body);
                    pricePromise.complete(price);
                } else {
                    pricePromise.fail("Failed to get latest stock price with status code: " + response.statusCode());
                }
            } else {
                pricePromise.fail(resp.cause());
            }
        });
        return future;
    }

    private String parsePrice(String body) {
        return "high price";
    }

    void close() {
        webClient.close();
        vertx.close();
    }
}
