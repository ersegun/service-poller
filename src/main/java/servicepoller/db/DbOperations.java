package servicepoller.db;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOptions;
import io.vertx.ext.sql.UpdateResult;

public class DbOperations {

    private static Integer QUERY_TIME_OUT = 5000;
    private JDBCClient client;


    public DbOperations(JDBCClient client) {
        this.client = client;
    }

    private Single<SQLConnection> getConnection() {
        return Single.create(single-> {
           client.getConnection(connection -> {
               if(connection.succeeded()) {
                   single.onSuccess(connection.result()
                           .setOptions(new SQLOptions().setQueryTimeout(QUERY_TIME_OUT)));
               }
               else {
                   single.onError(connection.cause());
               }
           });
        });
    }

    public Single<UpdateResult> updateWithParams(String statement, JsonArray array) {
        return getConnection().flatMap(sqlConnection -> Single.create(single -> {
                sqlConnection.updateWithParams(statement, array, result -> {
                   if(result.succeeded()) {
                       single.onSuccess(result.result());
                   }
                   else {
                       single.onError(result.cause());
                   }
                });
            })
        );
    }

    public Single<ResultSet> queryWithParams(String statement, JsonArray array) {
        return getConnection().flatMap(sqlConnection -> Single.create(single -> {
                    sqlConnection.queryWithParams(statement, array, result -> {
                        if(result.succeeded()) {
                            single.onSuccess(result.result());
                        }
                        else {
                            single.onError(result.cause());
                        }
                    });
                })
        );
    }
}
