package ctu.cict.khanhtypo.books;

import java.util.Date;
import java.util.Objects;

public class BookBuilder {
    private BookId id;
    private String title = "";
    private String isbn = "";
    private int pageCount;
    private Date publishedDate;
    private String thumbnailUrl = "";
    private String shortDescription = "";
    private String longDescription = "";
    private BookStatus status;
    private String[] authors = new String[0];
    private String[] categories = new String[0];

    public BookBuilder setId(BookId id) {
        //Objects.requireNonNull(id, "id is null");
        this.id = id;
        return this;
    }

    public BookBuilder setTitle(String title) {
        Objects.requireNonNull(title, "title is null");
        this.title = title;
        return this;
    }

    public BookBuilder setISBN(String isbn) {
        Objects.requireNonNull(isbn, "isbn is null");
        this.isbn = isbn;
        return this;
    }

    public BookBuilder setPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public BookBuilder setPublishedDate(Date publishedDate) {
        //Objects.requireNonNull(publishedDate, "publishedDate is null");
        this.publishedDate = publishedDate;
        return this;
    }

    public BookBuilder setThumbnailUrl(String thumbnailUrl) {
        if (thumbnailUrl != null)
            this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    public BookBuilder setShortDescription(String shortDescription) {
        if (shortDescription != null)
          this.shortDescription = shortDescription;
        return this;
    }

    public BookBuilder setLongDescription(String longDescription) {
        if (longDescription != null)
         this.longDescription = longDescription;
        return this;
    }

    public BookBuilder setStatus(BookStatus status) {
        Objects.requireNonNull(status, "status is null");
        this.status = status;
        return this;
    }

    public BookBuilder setAuthors(String[] authors) {

        this.authors = authors;
        return this;
    }

    public BookBuilder setCategories(String[] categories) {
        this.categories = categories;
        return this;
    }

    public Book createBook() {
        return new Book(id, title, isbn, pageCount, publishedDate, thumbnailUrl, shortDescription, longDescription, status, authors, categories);
    }
}