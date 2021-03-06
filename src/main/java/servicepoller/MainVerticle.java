package servicepoller;

import io.reactivex.Completable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import servicepoller.api.InsertUrlHandler;
import servicepoller.api.DeleteUrlHandler;
import servicepoller.api.GetUrlHandler;
import servicepoller.api.UpdateUrlHandler;
import servicepoller.db.DbOperations;
import servicepoller.db.SqliteDbConfiguration;
import servicepoller.log.ConsoleLogger;
import servicepoller.log.Logger;
import servicepoller.repository.SqlServicePollerRepository;
import servicepoller.repository.ServicePollerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

    private static final int SERVICE_PORT = 8080;

    private List<Callable<Completable>> onCloseFunctions = new ArrayList();
    private Logger logger = new ConsoleLogger();
    private ServicePollerRepository servicePollerRepository;

    @Override
    public void start(Future<Void> startFuture) {
        startCompletable().subscribe(startFuture::complete, startFuture::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        stopCompletable().subscribe(stopFuture::complete, stopFuture::fail);
    }

    private Completable startCompletable() {
        logger.info("Starting service-poller");
        SqliteDbConfiguration.initializeServicePollerDb(vertx, logger);
        servicePollerRepository = getServicePollerRepository();

        return setServicePollerJob()
                .andThen(startListening())
                .doOnComplete(() -> {
                    logger.info("Started listening on port " + SERVICE_PORT);
                });
    }

    private ServicePollerRepository getServicePollerRepository() {
        var client = SqliteDbConfiguration.getDatabaseClient(vertx);
        var dbOperations = new DbOperations(client);
        var servicePollerRepository = new SqlServicePollerRepository(dbOperations, logger);
        return servicePollerRepository;
    }

    private Completable setServicePollerJob() {
        var servicePollerJobHandler = new ServicePollerJobHandler(vertx, logger, servicePollerRepository);
        return servicePollerJobHandler.start()
                .doOnComplete(() -> {
                    var onCloseFunction = new Callable<Completable>() {
                        @Override
                        public Completable call() throws Exception {
                            return servicePollerJobHandler.stop();
                        }
                    };
                    onCloseFunctions.add(onCloseFunction);
                });
    }

    private Completable stopCompletable() {
        return Completable
                .concat(source -> onCloseFunctions
                        .stream()
                        .map(x -> Completable.fromCallable(x))
                        .collect(Collectors.toList()));
    }

    private Completable startListening() {
        var router = initializeAndGetRouter();

        return Completable.create(source -> {
            vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(SERVICE_PORT, "localhost", result -> {
                        if (result.succeeded()) {
                            source.onComplete();
                        } else {
                            source.onError(result.cause());
                        }
                    });
        });
    }

    private Router initializeAndGetRouter() {
        var router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/*").handler(StaticHandler.create());
        getService(router);
        insertService(router);
        updateService(router);
        deleteService(router);
        return router;
    }

    private void getService(Router router) {
        router.get("/urls").handler(new GetUrlHandler(servicePollerRepository, logger));
    }

    public void insertService(Router router) {
        router.post("/insert").handler(new InsertUrlHandler(servicePollerRepository, logger));
    }

    private void updateService(Router router) {
        router.put("/update").handler(new UpdateUrlHandler(servicePollerRepository, logger));
    }

    private void deleteService(Router router) {
        router.delete("/delete").handler(new DeleteUrlHandler(servicePollerRepository, logger));
    }
}
