package ctu.cict.khanhtypo.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DatabaseUtils {
    public static final String DATABASE_NAME = "Book";
    public static final String COLLECTION_BOOKS = "allBooks";
    private static final MongoDatabase database;


    public static MongoCollection<Document> getBooks() {
        return database.getCollection(COLLECTION_BOOKS);
    }

    public static void initiate() {}

    static {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        database = client.getDatabase(DATABASE_NAME);
        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
    }
}