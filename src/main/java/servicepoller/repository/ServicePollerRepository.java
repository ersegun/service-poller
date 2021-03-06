package servicepoller.repository;

import io.reactivex.Completable;
import io.reactivex.Single;
import servicepoller.model.ServiceUrl;
import java.util.List;

public interface ServicePollerRepository {
    Completable insertServiceUrl(ServiceUrl serviceUrl);

    Completable updateServiceUrl(ServiceUrl serviceUrl);

    Completable deleteServiceUrl(String id);

    Single<List<ServiceUrl>> getServiceUrls();
}
