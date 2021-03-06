package servicepoller.repository;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import servicepoller.db.DbOperations;
import servicepoller.helper.UrlHelper;
import servicepoller.log.Logger;
import servicepoller.model.ServiceUrl;
import servicepoller.model.ServiceUrlStatus;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SqlServicePollerRepository implements ServicePollerRepository {

    private DbOperations dbOperations;
    private Logger logger;

    public SqlServicePollerRepository(DbOperations dbOperations, Logger logger) {
        this.dbOperations = dbOperations;
        this.logger = logger;
    }

    @Override
    public Completable insertServiceUrl(ServiceUrl serviceUrl) {
        String sql = "INSERT INTO service_url (id, name, url, status, created, updated) VALUES (?, ?, ?, ?, ?, ?);";

        serviceUrl.setStatus(ServiceUrlStatus.UNKNOWN.status);
        serviceUrl.setCreated(LocalDateTime.now());
        serviceUrl.setUpdated(LocalDateTime.now());

        var jsonArray = new JsonArray()
                .add(UUID.randomUUID().toString())
                .add(serviceUrl.getName())
                .add(serviceUrl.getUrl())
                .add(serviceUrl.getStatus().status)
                .add(serviceUrl.getCreatedAsString())
                .add(serviceUrl.getUpdatedAsString());

        return dbOperations.updateWithParams(sql, jsonArray).ignoreElement();
    }

    @Override
    public Completable updateServiceUrl(ServiceUrl serviceUrl) {
        String sql = "UPDATE service_url SET name=?, url=?, status=?, created=?, updated=? WHERE id=?;";

        var jsonArray = new JsonArray()
                .add(serviceUrl.getName())
                .add(serviceUrl.getUrl())
                .add(serviceUrl.getStatus().status)
                .add(serviceUrl.getCreatedAsString())
                .add(serviceUrl.getUpdatedAsString())
                .add(serviceUrl.getId());

        return dbOperations.updateWithParams(sql, jsonArray).ignoreElement();
    }

    @Override
    public Completable deleteServiceUrl(String id) {
        String sql = "DELETE FROM service_url WHERE id=?;";

        var jsonArray = new JsonArray().add(id);

        return dbOperations.updateWithParams(sql, jsonArray).ignoreElement();
    }

    @Override
    public Single<List<ServiceUrl>> getServiceUrls() {
        String sql = "SELECT * FROM service_url;";

        return dbOperations.queryWithParams(sql, new JsonArray())
                .map(resultSet -> resultSet.getRows()
                        .stream()
                        .map(ServiceUrl::new)
                        .collect(Collectors.toList()))
                .doOnError(err -> {
                    logger.error(err);
                });
    }
}
