package servicepoller.api;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import servicepoller.helper.UrlHelper;
import servicepoller.log.Logger;
import servicepoller.model.ServiceUrl;
import servicepoller.model.ServiceUrlStatus;
import servicepoller.repository.ServicePollerRepository;

public class InsertUrlHandler implements Handler<RoutingContext> {

    private ServicePollerRepository servicePollerRepository;
    private Logger logger;

    public InsertUrlHandler(ServicePollerRepository servicePollerRepository, Logger logger) {
        this.servicePollerRepository = servicePollerRepository;
        this.logger = logger;
    }

    @Override
    public void handle(RoutingContext event) {
        var body = event.getBodyAsJson();
        var name = body.getString("name");
        var url = body.getString("url");
        var serviceUrl = new ServiceUrl(name, url);

        if(!UrlHelper.IsValidUrl(url)) {
            logger.info("Invalid url:" + url);
            return;
        }

        servicePollerRepository.insertServiceUrl(serviceUrl)
                .doOnError(err  -> logger.error(err))
                .subscribe(() -> {
                    event.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonObject().put("status", ServiceUrlStatus.OK.name()).encode());
                }, error -> {
                    logger.error(error);
                    event.response().setStatusCode(500)
                            .end("Internal Server Error");
                });
    }
}
