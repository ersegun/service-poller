package servicepoller.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import servicepoller.log.Logger;
import servicepoller.model.ServiceUrl;
import servicepoller.model.ServiceUrlStatus;
import servicepoller.repository.ServicePollerRepository;

public class UpdateUrlHandler implements Handler<RoutingContext> {

    private ServicePollerRepository servicePollerRepository;
    private Logger logger;

    public UpdateUrlHandler(ServicePollerRepository servicePollerRepository, Logger logger) {
        this.servicePollerRepository = servicePollerRepository;
        this.logger = logger;
    }

    @Override
    public void handle(RoutingContext event) {
        var serviceUrl = new ServiceUrl(event.getBodyAsJson());

        servicePollerRepository.updateServiceUrl(serviceUrl)
                .subscribe(() -> {
                    event.response()
                            .putHeader("content-type", "application/json")
                            .end(ServiceUrlStatus.OK.name());
                }, error -> {
                    logger.error(error);
                    event.response().setStatusCode(500)
                            .end("Internal Server Error");
                });
    }
}
