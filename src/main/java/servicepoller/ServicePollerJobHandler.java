package servicepoller;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import servicepoller.configuration.ServicePollerJobConfiguration;
import servicepoller.log.Logger;
import servicepoller.model.ServiceUrl;
import servicepoller.model.ServiceUrlStatus;
import servicepoller.repository.ServicePollerRepository;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class ServicePollerJobHandler implements Handler<Long> {
    private AtomicReference<Boolean> stopServicePollerJob = new AtomicReference<Boolean>(false);
    private AtomicReference<Boolean> isRunning = new AtomicReference<Boolean>(false);

    private Vertx vertx;
    private WebClient webClient;
    private Logger logger;
    private ServicePollerRepository servicePollerRepository;

    public ServicePollerJobHandler(Vertx vertx, Logger logger, ServicePollerRepository servicePollerRepository) {
        this.vertx = vertx;
        this.webClient = WebClient.create(vertx);
        this.logger = logger;
        this.servicePollerRepository = servicePollerRepository;
    }

    public Completable start() {
        return run().doOnComplete(this::scheduleRun);
    }

    public Completable stop() {
        return Completable.create(source -> {
            stopServicePollerJob.set(true);
        });
    }

    private Completable run() {
        if (!isRunning.get()) {
            isRunning.set(true);
            logger.info("Polling started");
            return servicePollerRepository
                    .getServiceUrls()
                    .flatMapObservable(Observable::fromIterable)
                    .flatMapSingle(url -> getStatus(url)
                            .map(result -> {
                                url.setStatus(result ? ServiceUrlStatus.OK.status : ServiceUrlStatus.FAILED.status);
                                url.setUpdated(LocalDateTime.now());
                                return url;
                            })).toList()
                    .flatMapCompletable(serviceUrlList -> {
                        if (serviceUrlList.size() == 0)
                            return Completable.complete();
                        var completable = servicePollerRepository
                                .updateServiceUrl(serviceUrlList.get(0));
                        for (int i = 1; i < serviceUrlList.size(); i++) {
                            completable = completable.andThen(servicePollerRepository
                                    .updateServiceUrl(serviceUrlList.get(i)));
                        }
                        return completable;
                    })
                    .doOnComplete(() -> {
                        logger.info("Polling ended");
                        isRunning.set(false);
                    });
        } else
            return Completable.complete();
    }


    private Single<Boolean> getStatus(ServiceUrl serviceUrl) {
        return Single.create(source -> {
            if (source.isDisposed())
                return;

            HttpRequest<Buffer> request = webClient.getAbs(serviceUrl.getUrl()).timeout(ServicePollerJobConfiguration.TIMEOUT);
            request.send(ar -> {
                if (ar.succeeded()) {
                    source.onSuccess(true);
                } else {
                    source.onSuccess(false);
                }
            });
        });
    }

    private void scheduleRun() {
        if (!stopServicePollerJob.get()) {
            vertx.setTimer(ServicePollerJobConfiguration.PERIOD.toMillis(), this);
        }
    }

    @Override
    public void handle(Long event) {
        run().subscribe(this::scheduleRun, (e) -> {
            logger.error(e);
        });
    }
}
