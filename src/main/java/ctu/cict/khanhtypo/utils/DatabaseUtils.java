package ctu.cict.khanhtypo.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DatabaseUtils {
    private static MongoClient mongoClient;
    public static final String DATABASE_NAME = "Book";
    private static final MongoDatabase database;
    public static final String COLLECTION_BOOKS = "allBooks";


    public static MongoCollection<Document> getBooks() {
        return database.getCollection(COLLECTION_BOOKS);
    }

    static {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        database = client.getDatabase(DATABASE_NAME);
        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
        mongoClient = client;
    }

}
