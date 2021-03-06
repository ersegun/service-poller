package servicepoller.model;

import io.vertx.core.json.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceUrl {
    String id;
    String name;
    String url;
    ServiceUrlStatus status;
    LocalDateTime created;
    LocalDateTime updated;


    public ServiceUrl(JsonObject body) {
        this.id = body.getString("id");
        this.name = body.getString("name");
        this.url = body.getString("url");
        this.status = ServiceUrlStatus.getServiceUrlStatus(body.getInteger("status"));
        this.created = getDateFromString(body.getString("created"));
        this.updated = getDateFromString(body.getString("updated"));
    }

    public ServiceUrl(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getId()  {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ServiceUrlStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public String getCreatedAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return getCreated().format(formatter);
    }

    public String getUpdatedAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return getUpdated().format(formatter);
    }

    public void setCreated(LocalDateTime createdDateTime) {
        this.created = createdDateTime.now();
    }

    public void setUpdated(LocalDateTime updatedDateTime) {
        this.updated = updatedDateTime.now();
    }

    public void setStatus(int status) {
        this.status = ServiceUrlStatus.getServiceUrlStatus(status);
    }

    private LocalDateTime getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(date, formatter);
    }
}
