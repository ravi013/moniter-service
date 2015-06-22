package xyz;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.net.ConnectException;
import java.util.Random;

/**
 * Created by Ravi on 6/19/2015.
 */
public class Master extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(Master.class);
    public static final String REGISTER_ADDR = "register.addr";
    public static final String DISCOVERY_ADDR = "discovery.addr";

    private int port = 8080;
    private String host = "localhost";
    public static void main(String[] args) {
        VertxOptions vertOptions =new VertxOptions();

        vertOptions.setClustered(true);
        Vertx.clusteredVertx(vertOptions, res -> {
            Vertx vertx = res.result();
            vertx.deployVerticle(Master.class.getCanonicalName());
        });
    }
    @Override
    public void start() throws Exception {
        log.info("Starting master verticle!!");
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("port", port);
        jsonObject.put("host", host);
        jsonObject.put("nodeId", ((VertxInternal)vertx).getNodeID());

        vertx.eventBus().consumer(REGISTER_ADDR, response -> {
            JsonObject serviceInfo = (JsonObject) response.body();
            log.info("Received serviceInfo " + serviceInfo);
            JsonObject replyObj = new JsonObject();
            replyObj.put("status", "ok");
            response.reply(replyObj);
        });

        vertx.setPeriodic(5000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {
                log.info("Publishing discovery ");
                vertx.eventBus().publish(DISCOVERY_ADDR, jsonObject);
            }
        });
    }
}
