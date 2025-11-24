package ctu.cict.khanhtypo.forms;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface IBookDB {
    void deleteBookEntry(Book book);

    void addBookEntry(Book book) throws MongoWriteException;

    default MongoCollection<Document> getCollection() {
        return DatabaseUtils.getBooks();
    }

    void searchBooks(Bson filter);
}
