/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import librarymanager.entities.Book;
import librarymanager.managers.interfaces.BookManager;

/**
 *
 * @author xlauren1
 */
public class BookManagerImpl implements BookManager {

    private final DataSource dataSource;

    public BookManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createBook(Book book) throws FailureException {

        validate(book);

        if (book.getId() != null) {
            throw new IllegalArgumentException("book has already been initialized");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO BOOK (name, author, isbn) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, book.getName());
            st.setString(2, book.getAuthor());
            st.setString(3, book.getIsbn());

            int addedRows = st.executeUpdate();

            if (addedRows != 1) {
                throw new FailureException("error in inserting book, was"
                        + " added " + addedRows + "rows");
            }

            ResultSet rs = st.getGeneratedKeys();
            book.setId(getKey(rs, book));

        } catch (SQLException ex) {
            throw new FailureException("error in inserting: " + book, ex);
        }
    }

    public void validate(Book book) throws IllegalArgumentException {
        if (book == null) {
            throw new IllegalArgumentException("grave is null");
        }
        if (book.getName() == null) {
            throw new IllegalArgumentException("grave is null");
        }
        if (book.getIsbn() == null) {
            throw new IllegalArgumentException("grave column is negative number");
        }
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("grave row is negative number");
        }
    }

    public Long getKey(ResultSet rs, Book book) {
        return null;
    }

    @Override
    public void updateBook(Book book) throws FailureException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteBook(Book book) throws FailureException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Book findBookById(long id) throws FailureException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Book> listAllBooks() throws FailureException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
