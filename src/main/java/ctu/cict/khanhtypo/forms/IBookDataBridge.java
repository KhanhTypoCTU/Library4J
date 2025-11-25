package ctu.cict.khanhtypo.forms;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface IBookDataBridge {
    void deleteBookEntry(BookEntry book);

    void addBookEntry(Book book) throws MongoWriteException;

    //return the queried books count
    int searchBooks(Bson filter);

    void updateBookEntry(BookEntry book, Book updatedBook);

    default MongoCollection<Document> getCollection() {
        return DatabaseUtils.getBooks();
    }

}
