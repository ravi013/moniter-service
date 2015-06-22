package xyz;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

import java.net.ServerSocket;

/**
 * Created by Ravi on 6/19/2015.
 */
public class Service extends AbstractVerticle {
    private Logger log ;
    public static final String REGISTER_ADDR = "register.addr";
    public static final String DISCOVERY_ADDR = "discovery.addr";
   public Service(){
       log = LoggerFactory.getLogger(getClass());
    }

    public static void main(String[] args) {
        VertxOptions vertOptions =new VertxOptions();

        vertOptions.setClustered(true);
        Vertx.clusteredVertx(vertOptions, res -> {
            Vertx vertx = res.result();
            vertx.deployVerticle(Service.class.getCanonicalName());
        });
    }

    @Override
    public void start() throws Exception {
        startServer();
    }

    private void startServer() {

        HttpServer server = vertx.createHttpServer();
        int port = findRandomPort();
        log.info("Starting server on port " + port);
        ServiceInfo service = new ServiceInfo();
        service.setPort(port);
        service.setNodeId(((VertxInternal)vertx).getNodeID());
        sendRegisterStatus(service);
        this.getVertx().eventBus().consumer(DISCOVERY_ADDR, response -> {
            log.info("New master found " + response.body() + " sending register info");
            sendRegisterStatus(service);
        });
        server.requestHandler(req -> {
        //do something later
        }).listen(port);
    }

    private void sendRegisterStatus(ServiceInfo service) {
        long startTime=System.currentTimeMillis();
        this.getVertx().eventBus().send(REGISTER_ADDR, service.toJson(), res -> {

            long endTime=System.currentTimeMillis();
            log.info("Time taken to send " +service.toJson()+ "is "+(endTime-startTime));
            if (res.succeeded()) {
                log.info("Register Service Response" + (JsonObject)res.result().body());
            } else {
                log.warn(res.cause());
            }
        });
    }

    private int  findRandomPort() {
        int port=-1;
        try {
            ServerSocket socket = new ServerSocket(0);
            port= socket.getLocalPort();
            socket.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return port;
    }
}
