import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import servicepoller.MainVerticle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestServices {

    private final String HOST = "localhost";
    private final Integer PORT = 8080;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // TODO: Use test db, do not directly interact with actual db.
    @BeforeEach
    void init(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle() {
        }, testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    @DisplayName("Testing insert/update/delete/get services")
    @Timeout(value = 30, timeUnit = TimeUnit.SECONDS)
    void testServices(Vertx vertx, VertxTestContext testContext) {
        WebClient.create(vertx)
                .get(PORT, HOST, "/urls")
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    JsonArray body = response.result().bodyAsJsonArray();

                    Assertions.assertNotNull(body);

                    insertService(vertx, testContext, body.size());
                }));
    }

    private void insertService(Vertx vertx, VertxTestContext testContext, int bodySize) {
        WebClient.create(vertx)
                .post(PORT, HOST, "/insert")
                .sendJsonObject(new JsonObject()
                        .put("id", UUID.randomUUID().toString())
                        .put("name", "Kry")
                        .put("url", "https://www.kry.se/")
                        .put("status", 1)
                        .put("created", LocalDateTime.now().format(formatter))
                        .put("updated", LocalDateTime.now().format(formatter)), response -> {
                    if (response.succeeded()) {
                        assertEquals(200, response.result().statusCode());
                        getService(vertx, testContext, bodySize + 1, false, true);
                    }
                });
    }

    private void getService(Vertx vertx, VertxTestContext testContext, int count, boolean delete, boolean update) {
        WebClient.create(vertx)
                .get(PORT, HOST, "/urls")
                .send(response -> {
                    assertEquals(200, response.result().statusCode());
                    JsonArray body = response.result().bodyAsJsonArray();
                    assertEquals(count, body.size());

                    if (delete) {
                        var jsonObject = body.getJsonObject(count - 1);
                        deleteService(jsonObject.getString("id"), vertx, testContext, body.size());
                    } else if (update) {
                        var jsonObject = body.getJsonObject(count - 1);
                        jsonObject.put("name", "Updated Kry");
                        jsonObject.put("url", "https://www.kry.com");
                        jsonObject.put("status", 2);
                        jsonObject.put("created", LocalDateTime.now().format(formatter));
                        jsonObject.put("updated", LocalDateTime.now().format(formatter));

                        updateService(vertx, testContext, jsonObject, body.size());
                    } else {
                        testContext.completeNow();
                    }
                });
    }

    private void updateService(Vertx vertx, VertxTestContext testContext, JsonObject updatedUrl, int bodySize) {
        WebClient.create(vertx)
                .put(PORT, HOST, "/update")
                .sendJsonObject(updatedUrl, response -> {
                    if (response.succeeded()) {
                        assertEquals(200, response.result().statusCode());
                        getService(vertx, testContext, bodySize, true, false);
                    }
                });
    }

    private void deleteService(String id, Vertx vertx, VertxTestContext testContext, int bodySize) {
        WebClient.create(vertx)
                .delete(PORT, HOST, "/delete")
                .sendJsonObject(new JsonObject().put("id", id), response -> {
                    if (response.succeeded()) {
                        assertEquals(200, response.result().statusCode());
                        getService(vertx, testContext, bodySize - 1, false, false);
                    }
                });
    }
}