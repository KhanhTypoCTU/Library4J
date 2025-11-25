package ctu.cict.khanhtypo.books;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class Book {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MMMM dd, yyyy");
    private final String title;
    private final String isbn;
    private final int pageCount;
    private final Date publishedDate;
    private final BookStatus status;
    private final String[] authors;
    private final String[] categories;
    private BookId deferredBookId;

    public Book(String title, String isbn, int pageCount, Date publishedDate,
                BookStatus status, String[] authors, String[] categories) {
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.publishedDate = publishedDate;
        this.status = status;
        this.authors = authors;
        this.categories = categories;
    }

    private Book loadBookId(Document source) {
        if (!source.containsKey("_id"))
            System.out.println("Book \"" + this.title + "\" is being marked for creation, deferring BookId...");
        else setBookId(BookId.from(source.get("_id")));
        return this;
    }

    public void setBookId(BookId id) {
        this.deferredBookId = id;
    }

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
        try {
            String title = document.getString("title");
            String ISBN = document.getString("isbn");
            int pageCount = document.getInteger("pageCount");
            Date publishedDate = document.getDate("publishedDate");
            String status = document.getString("status");
            BookStatus bookStatus = BookStatus.valueOf(status);
            List<String> authors = document.getList("authors", String.class);
            List<String> categories = document.getList("categories", String.class);
            return new BookBuilder()
                    .setTitle(title)
                    .setISBN(ISBN)
                    .setPageCount(pageCount)
                    .setPublishedDate(publishedDate)
                    .setStatus(bookStatus)
                    .setAuthors(authors.toArray(String[]::new))
                    .setCategories(categories.toArray(String[]::new))
                    .createBook().loadBookId(document);
        } catch (ClassCastException e) {
            System.out.println("Cast Exception caught: " + document);
        }
        return null;
    }


    public Document toDocument() {
        return new Document()
                .append("title", title)
                .append("isbn", isbn)
                .append("pageCount", pageCount)
                .append("publishedDate", publishedDate)
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

    public BookId id() {
        return this.deferredBookId;
    }

    public String title() {
        return title;
    }

    public String isbn() {
        return isbn;
    }

    public int pageCount() {
        return pageCount;
    }

    public Date publishedDate() {
        return publishedDate;
    }

    public BookStatus status() {
        return status;
    }

    public String authors() {
        return StringUtils.join(authors, ", ");
    }

    public String categories() {
        return StringUtils.join(categories, ", ");
    }
}