package ctu.cict.khanhtypo.utils;

import com.google.common.collect.Iterables;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import ctu.cict.khanhtypo.users.User;
import org.bson.Document;

public class DatabaseUtils {
    public static final String ACCOUNTS_COLLECTION = "Accounts";
    private static MongoDatabase database;

    public static void setDatabase(MongoDatabase db) {
        database = db;
    }

    public static MongoCollection<User> getAccounts() {
        if (!Iterables.any(database.listCollectionNames(), name -> name.equals(ACCOUNTS_COLLECTION)))
            database.createCollection(ACCOUNTS_COLLECTION);
        return database.getCollection(ACCOUNTS_COLLECTION, User.class);
    }
}
