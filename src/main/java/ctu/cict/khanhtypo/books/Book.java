package ctu.cict.khanhtypo.books;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonInt32;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public record Book(
        @BsonProperty("_id") ObjectId _id,
        @BsonProperty("title") String title,
        @BsonProperty("isbn") String ISBN,
        @BsonProperty("pageCount") BsonInt32 pageCount,
        @BsonProperty("publishedDate") BsonDateTime publishedDate,
        @BsonProperty("thumbnailUrl") String thumbnailUrl,
        @BsonProperty("shortDescription") String shortDescription,
        @BsonProperty("longDescription") String longDescription,
        @BsonProperty("status") String status,
        @BsonProperty("authors") BsonArray authors,
        @BsonProperty("categories") BsonArray categories
) {
}