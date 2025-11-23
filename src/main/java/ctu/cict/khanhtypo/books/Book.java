package ctu.cict.khanhtypo.books;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public record Book(BookId id, String title, String isbn, int pageCount, Date publishedDate, String thumbnailUrl,
                   String shortDescription, String longDescription, BookStatus status, String[] authors,
                   String[] categories) {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MMMM dd, yyyy");

    public String isbnString() {
        return isbn == null ? "none" : isbn;
    }

    public String authorsString() {
        return ArrayUtils.isEmpty(authors) ? "none" : String.join(", ", authors);
    }

    public String categoriesString() {
        return ArrayUtils.isEmpty(categories) ? "none" : String.join(", ", categories);
    }

    public static Book fromDocument(Document document) {
        return fromDocument(document, false);
    }

    public static Book fromDocument(Document document, boolean deferred) {
        try {
            BookId id = BookId.from(document.get("_id"), deferred);
            String title = document.getString("title");
            String ISBN = document.getString("isbn");
            int pageCount = document.getInteger("pageCount");
            Date publishedDate = document.getDate("publishedDate");
            String thumbnailUrl = document.getString("thumbnailUrl");
            String shortDescription = document.getString("shortDescription");
            String longDescription = document.getString("longDescription");
            String status = document.getString("status");
            BookStatus bookStatus = BookStatus.valueOf(status);
            List<String> authors = document.getList("authors", String.class);
            List<String> categories = document.getList("categories", String.class);
            return new BookBuilder()
                    .setId(id)
                    .setTitle(title)
                    .setISBN(ISBN)
                    .setPageCount(pageCount)
                    .setPublishedDate(publishedDate)
                    .setThumbnailUrl(thumbnailUrl)
                    .setShortDescription(shortDescription)
                    .setLongDescription(longDescription)
                    .setStatus(bookStatus)
                    .setAuthors(authors.toArray(String[]::new))
                    .setCategories(categories.toArray(String[]::new))
                    .createBook();
        } catch (ClassCastException e) {
            System.out.println("Cast Exception caught: " + document);
        }
        return null;
    }


    public Document toDocument() {
        return new Document()
                //.append("_id", id.getAsGenericObject())
                .append("title", title)
                .append("isbn", isbn)
                .append("pageCount", pageCount)
                .append("publishedDate", publishedDate)
                .append("thumbnailUrl", thumbnailUrl)
                .append("shortDescription", shortDescription)
                .append("longDescription", longDescription)
                .append("status", status)
                .append("authors", List.of(authors))
                .append("categories", List.of(categories));
    }

    public String statusString() {
        return this.status.getDisplayText();
    }

    public String dateString() {
        return this.publishedDate == null ? "no data" : DATE_FORMATTER.format(this.publishedDate);
    }
}