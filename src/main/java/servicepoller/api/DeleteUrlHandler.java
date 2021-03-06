package servicepoller.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import servicepoller.log.Logger;
import servicepoller.model.ServiceUrlStatus;
import servicepoller.repository.ServicePollerRepository;

public class DeleteUrlHandler implements Handler<RoutingContext> {

    private ServicePollerRepository servicePollerRepository;
    private Logger logger;

    public DeleteUrlHandler(ServicePollerRepository servicePollerRepository, Logger logger) {
        this.servicePollerRepository = servicePollerRepository;
        this.logger = logger;
    }

    @Override
    public void handle(RoutingContext event) {
        var id = event.getBodyAsJson().getString("id");

        servicePollerRepository.deleteServiceUrl(id)
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
