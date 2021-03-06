package servicepoller.db;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import servicepoller.log.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SqliteDbConfiguration {

    private static final String DB_PATH = "service-poller.db";
    private static final String CREATE_TABLE_SQL_PATH = "src/main/resources/create-service-poller-table.sql";
    //private static final String CLEAN_TABLE_SQL_PATH = "src/main/resources/clean-service-poller-table.sql";

    private static JDBCClient client;

    public static JDBCClient getDatabaseClient(Vertx vertx) {
        if (client == null) {
            client = JDBCClient.createShared(vertx, new JsonObject()
                    .put("url", "jdbc:sqlite:" + DB_PATH)
                    .put("driver_class", "org.sqlite.JDBC")
                    .put("max_pool_size", 30));
        }
        return client;
    }

    public static void initializeServicePollerDb(Vertx vertx, Logger logger) {
        var client = getDatabaseClient(vertx);

        var sql = getCreateServicePollerSql();

        client.queryWithParams(sql, new JsonArray(), result -> {
            if (result.failed()) {
                logger.error(result.cause());
            }
        });
    }

    private static String getCreateServicePollerSql() {
        try {
            return Files.readString(Paths.get(CREATE_TABLE_SQL_PATH), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return "";
        }
    }
}
