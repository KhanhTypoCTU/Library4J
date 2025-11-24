package ctu.cict.khanhtypo.books;

import java.util.Date;
import java.util.Objects;

public class BookBuilder {
    private String title = "";
    private String isbn = "";
    private int pageCount;
    private Date publishedDate;
    private BookStatus status;
    private String[] authors = new String[0];
    private String[] categories = new String[0];

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
        return new Book(title, isbn, pageCount, publishedDate, status, authors, categories);
    }
}