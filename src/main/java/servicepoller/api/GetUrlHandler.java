package servicepoller.api;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import servicepoller.log.Logger;
import servicepoller.model.ServiceUrl;
import servicepoller.repository.ServicePollerRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GetUrlHandler implements Handler<RoutingContext> {

    private ServicePollerRepository servicePollerRepository;
    private Logger logger;

    public GetUrlHandler(ServicePollerRepository servicePollerRepository, Logger logger) {
        this.servicePollerRepository = servicePollerRepository;
        this.logger = logger;
    }

    @Override
    public void handle(RoutingContext event) {
        servicePollerRepository.getServiceUrls()
                .doOnError(err -> logger.error(err))
                .subscribe(response -> {
                    event.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonArray(toJsonList(response)).encode());
                }, error -> {
                    logger.error(error);
                    event.response().setStatusCode(500)
                            .end("Internal Server Error");
                });
    }

    private List<JsonObject> toJsonList(List<ServiceUrl> serviceUrls) {
        return serviceUrls.stream().map(url ->
                new JsonObject()
                        .put("id", url.getId())
                        .put("name", url.getName())
                        .put("url", url.getUrl())
                        .put("status", url.getStatus().toString())
                        .put("created", url.getCreated().toString())
                        .put("updated", url.getUpdated().toString()))
                .collect(Collectors.toList());
    }
}
