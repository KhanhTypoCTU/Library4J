package ctu.cict.khanhtypo.utils;

import com.mongodb.client.MongoDatabase;

public class DatabaseUtils {
    private static MongoDatabase database;

    public static void setDatabase(MongoDatabase db) {
        database = db;
    }

}
