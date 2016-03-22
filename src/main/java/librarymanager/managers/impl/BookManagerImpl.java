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
import java.util.ArrayList;
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
                        "INSERT INTO BOOK (NAME, AUTHOR, ISBN) VALUES (?,?,?)",
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

    public Long getKey(ResultSet rs, Book book) throws FailureException, SQLException {
        if (rs.next()) {
            if (rs.getMetaData().getColumnCount() != 1) {
                throw new FailureException(" wrong key fields count: " + rs.getMetaData().getColumnCount());
            }

            Long key = rs.getLong(1);
            if (rs.next()) {
                throw new FailureException("error with generating keys, in process"
                        + " of inserting book, more than 1 key found");
            }
            return key;
        } else {
            throw new FailureException("error with generating keys, in process"
                    + " of inserting book, no keys found");
        }
    }

    @Override
    public void updateBook(Book book) throws FailureException {
        validate(book);

        if (book.getId() == null) {
            throw new IllegalArgumentException("book id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE BOOK SET NAME = ?, AUTHOR = ?, ISBN = ? WHERE ID = ?")) {

            st.setLong(1, book.getId());
            st.setString(2, book.getName());
            st.setString(3, book.getAuthor());
            st.setString(4, book.getIsbn());

            int count = st.executeUpdate();

            if (count == 0) {
                throw new NotFoundException("book was not found in database");
            } else if (count != 1) {
                throw new FailureException("error in updating book, was updated "
                        + "more than 1 row, was " + count);
            }
        } catch (SQLException ex) {
            throw new FailureException("error when updating book, " + book);
        }
    }

    @Override
    public void deleteBook(Book book) throws FailureException {
        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getId() == null) {
            throw new IllegalArgumentException("book id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM BOOK WHERE ID = ?")) {

            st.setLong(1, book.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new NotFoundException("book was not found in database");
            } else if (count != 1) {
                throw new FailureException("error when deleting book,"
                        + "was deleted more books than 1, was " + count);
            }
        } catch (SQLException ex) {
            throw new FailureException("Error in deleting book " + book, ex);
        }
    }

    @Override
    public Book findBookById(Long id) throws FailureException {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BOOK WHERE ID = ?")) {

            st.setLong(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Book book = resultToBook(rs);
                if (rs.next()) {
                    throw new FailureException("More entities with same id");
                }

                return book;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new FailureException("error in finding book by id: " + id, ex);
        }
    }

    @Override
    public List<Book> listAllBooks() throws FailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BOOK ")) {

            ResultSet rs = st.executeQuery();
            List<Book> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultToBook(rs));
            }

            return result;
        } catch (SQLException ex) {
            throw new FailureException("error when listing all books ", ex);
        }
    }

    private Book resultToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
       
        book.setId(rs.getLong("ID"));
        book.setName(rs.getString("NAME"));
        book.setAuthor(rs.getString("AUTHOR"));
        book.setIsbn(rs.getString("ISBN"));
        
        return book;
    }

}
