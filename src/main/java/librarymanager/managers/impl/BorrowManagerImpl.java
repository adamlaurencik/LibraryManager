/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import librarymanager.entities.Book;
import librarymanager.entities.Borrow;
import librarymanager.entities.Customer;
import librarymanager.managers.interfaces.BookManager;
import librarymanager.managers.interfaces.BorrowManager;
import librarymanager.managers.interfaces.CustomerManager;

/**
 *
 * @author xlauren1
 */
public class BorrowManagerImpl implements BorrowManager {

    private final DataSource dataSource;
    private final BookManager bookManager;
    private final CustomerManager customerManager;

    public BorrowManagerImpl(DataSource dataSource, BookManager bookManager, CustomerManager customerManager) {
        this.dataSource = dataSource;
        this.bookManager = bookManager;
        this.customerManager = customerManager;
    }

    @Override
    public void createBorrow(Borrow borrow) {

        validate(borrow);

        if (borrow.getId() != null) {
            throw new IllegalArgumentException("borrow has already been initialized");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO BORROW (book_id, customer_id, borrow_date, return_date, returned) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            st.setLong(1, borrow.getBook().getId());
            st.setLong(2, borrow.getCustomer().getId());
            st.setDate(3, Date.valueOf(borrow.getBorrowDate()));
            st.setDate(4, Date.valueOf(borrow.getReturnDate()));
            st.setBoolean(5, false);

            int addedRows = st.executeUpdate();

            if (addedRows != 1) {
                throw new FailureException("error in inserting borrow, was"
                        + " added " + addedRows + "rows");
            }

            ResultSet rs = st.getGeneratedKeys();
            borrow.setId(getKey(rs));

        } catch (SQLException ex) {
            throw new FailureException("error in inserting: " + borrow, ex);
        }
    }

    private void validate(Borrow borrow) throws IllegalArgumentException {
        if (borrow == null) {
            throw new IllegalArgumentException("borrow is null");
        }
        if (borrow.getBook() == null || borrow.getBook().getId() == null) {
            throw new IllegalArgumentException("borrow book is null or book is not in database");
        }
        if (borrow.getCustomer() == null || borrow.getCustomer().getId() == null) {
            throw new IllegalArgumentException("borrow customer is null or customer is not in database");
        }
        if (borrow.getBorrowDate() == null) {
            throw new IllegalArgumentException("borrow date was not specified");
        }
        if (borrow.getReturnDate() == null) {
            throw new IllegalArgumentException("return date was not specified");
        }
        if (borrow.getBorrowDate().toEpochDay() > borrow.getReturnDate().toEpochDay()) {
            throw new IllegalArgumentException("borrow day is bigger than return day");
        }
    }

    public Long getKey(ResultSet rs) throws FailureException, SQLException {
        if (rs.next()) {
            if (rs.getMetaData().getColumnCount() != 1) {
                throw new FailureException(" wrong key fields count: " + rs.getMetaData().getColumnCount());
            }

            Long key = rs.getLong(1);
            if (rs.next()) {
                throw new FailureException("error with generating keys, in process"
                        + " of inserting borrow, more than 1 key found");
            }
            return key;
        } else {
            throw new FailureException("error with generating keys, in process"
                    + " of inserting book, no keys found");
        }
    }

    @Override
    public void updateBorrow(Borrow borrow) {
        validate(borrow);

        if (borrow.getId() == null) {
            throw new IllegalArgumentException("borrow id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE BORROW SET book_id = ?, customer_id = ?, borrow_date = ? return_date, returned= ? WHERE ID = ?")) {

            st.setLong(1, borrow.getId());
            st.setLong(2, borrow.getId());
            st.setDate(3, java.sql.Date.valueOf(borrow.getBorrowDate()));
            st.setDate(4, java.sql.Date.valueOf(borrow.getReturnDate()));
            st.setBoolean(5, borrow.isReturned());
            st.setLong(6, borrow.getId());

            int count = st.executeUpdate();

            if (count == 0) {
                throw new NotFoundException("borrow was not found in database");
            } else if (count != 1) {
                throw new FailureException("error in updating borrow, was updated "
                        + "more than 1 row, was " + count);
            }
        } catch (SQLException ex) {
            throw new FailureException("error when updating borrow, " + borrow, ex);
        }
    }

    @Override
    public void deleteBorrow(Borrow borrow) {
        if (borrow == null) {
            throw new IllegalArgumentException("borrow is null");
        }
        if (borrow.getId() == null) {
            throw new IllegalArgumentException("borrow id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM BORROW WHERE ID = ?")) {

            st.setLong(1, borrow.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new NotFoundException("borrow was not found in database");
            } else if (count != 1) {
                throw new FailureException("error when deleting borrow,"
                        + "was deleted more borrows than 1, was " + count);
            }
        } catch (SQLException ex) {
            throw new FailureException("Error in deleting book " + borrow, ex);
        }
    }

    @Override
    public Borrow findBorrowById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BORROW WHERE ID = ?")) {

            st.setLong(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Borrow borrow = resultToBorrow(rs);
                if (rs.next()) {
                    throw new FailureException("More entities with same id");
                }

                return borrow;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new FailureException("error in finding book by id: " + id, ex);
        }
    }

    @Override
    public List<Borrow> findBorrowForCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is null");
        }

        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer's id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BORROW WHERE CUSTOMER_ID= ?")) {
            st.setLong(1, customer.getId());
            ResultSet rs = st.executeQuery();
            List<Borrow> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultToBorrow(rs));
            }

            return result;
        } catch (SQLException ex) {
            throw new FailureException("error when listing all borrows for customer " + customer, ex);
        }
    }

    @Override
    public List<Borrow> findBorrowForBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book is null");
        }

        if (book.getId() == null) {
            throw new IllegalArgumentException("Book's id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BORROW WHERE BOOK_ID= ?")) {
            st.setLong(1, book.getId());
            ResultSet rs = st.executeQuery();
            List<Borrow> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultToBorrow(rs));
            }

            return result;
        } catch (SQLException ex) {
            throw new FailureException("error when listing all borrows for book " + book, ex);
        }
    }

    @Override
    public List<Book> listBorrowedBooks() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT DISTINCT(BOOK_ID) FROM BORROW")) {
            ResultSet rs = st.executeQuery();
            List<Book> result = new ArrayList<>();

            while (rs.next()) {
                result.add(bookManager.findBookById(rs.getLong("BOOK_ID")));
            }

            return result;
        } catch (SQLException ex) {
            throw new FailureException("error when listing all borrowed books ", ex);
        }
    }

    @Override
    public boolean isBorrowed(Book book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Borrow> listAllBorrows() throws FailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BORROW ")) {

            ResultSet rs = st.executeQuery();
            List<Borrow> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultToBorrow(rs));
            }

            return result;
        } catch (SQLException ex) {
            throw new FailureException("error when listing all borrows ", ex);
        }
    }

    private Borrow resultToBorrow(ResultSet rs) throws SQLException {
        Borrow borrow = new Borrow();

        borrow.setId(rs.getLong("ID"));
        borrow.setBook(bookManager.findBookById(rs.getLong("BOOK_ID")));
        borrow.setCustomer(customerManager.findCustomerById(rs.getLong("CUSTOMER_ID")));
        borrow.setBorrowDate(rs.getDate("BORROW_DATE").toLocalDate());
        borrow.setReturnDate(rs.getDate("RETURN_DATE").toLocalDate());
        borrow.setReturned(rs.getBoolean("RETURNED"));
        return borrow;
    }

}
