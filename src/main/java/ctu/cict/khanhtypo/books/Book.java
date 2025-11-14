package ctu.cict.khanhtypo.books;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public record Book(BookId id, String title, String ISBN, int pageCount, Date publishedDate,
                   String thumbnailUrl, String shortDescription, String longDescription, String status,
                   String[] authors, String[] categories) {

    public String authorsString() {
        return ArrayUtils.isEmpty(authors) ? "" : String.join(", ", authors);
    }

    public String categoriesString() {
        return ArrayUtils.isEmpty(categories) ? "" : String.join(", ", categories);
    }

    public static Book fromDocument(Document document) {
        try {
            BookId id = BookId.from(document.get("_id"));
            String title = document.getString("title");
            String ISBN = document.getString("ISBN");
            int pageCount = document.getInteger("pageCount");
            Date publishedDate = document.getDate("publishedDate");
            String thumbnailUrl = document.getString("thumbnailUrl");
            String shortDescription = document.getString("shortDescription");
            String longDescription = document.getString("longDescription");
            String status = document.getString("status");
            List<String> authors = document.getList("authors", String.class);
            List<String> categories = document.getList("categories", String.class);
            return new Book(id, title, ISBN, pageCount, publishedDate, thumbnailUrl, shortDescription, longDescription, status, authors.toArray(String[]::new), categories.toArray(String[]::new));
        } catch (ClassCastException e) {
            System.out.println("Cast Exception caught: " + document);
        }
        return null;
    }


    public Document toDocument() {
        return new Document()
                .append("_id", id.getAsObject())
                .append("title", title)
                .append("ISBN", ISBN)
                .append("pageCount", pageCount)
                .append("publishedDate", publishedDate)
                .append("thumbnailUrl", thumbnailUrl)
                .append("shortDescription", shortDescription)
                .append("longDescription", longDescription)
                .append("status", status)
                .append("authors", List.of(authors))
                .append("categories", List.of(categories));
    }
}