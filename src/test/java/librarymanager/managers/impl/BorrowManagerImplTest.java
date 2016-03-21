/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import librarymanager.entities.Book;
import librarymanager.entities.Borrow;
import librarymanager.entities.Customer;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Adam Laurenčík
 */
public class BorrowManagerImplTest {

    private DataSource dataSource;
    private BorrowManagerImpl manager;
    private BookManagerImpl bookManager;
    private CustomerManagerImpl customerManager;
    private Book book1 = new Book();
    private Customer customer1 = new Customer();

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE BOOK( "
                    + "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "NAME VARCHAR(50), "
                    + "AUTHOR VARCHAR(50), "
                    + "ISBN VARCHAR(50))").executeUpdate();
        }
        manager = new BorrowManagerImpl();
        bookManager = new BookManagerImpl(dataSource);
        customerManager = new CustomerManagerImpl();

        book1.setName("Effective Java");
        book1.setAuthor("Joshua Bloch");
        book1.setIsbn("978-0321356680");

        customer1.setName("Milan");
        customer1.setSurname("Pazitka");
        customer1.setAddress("Uherske Hradiste");
        customer1.setPhone("+421915538112");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteBorrowWithNull() throws Exception {
        manager.deleteBorrow(null);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DROP TABLE BOOK").executeUpdate();
        }
    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:bookManagerImpl-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    private static Borrow newBorrow(Book book, Customer customer,
            LocalDate borrowDate, LocalDate returnDate) {
        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setCustomer(customer);
        borrow.setBorrowDate(borrowDate);
        borrow.setReturnDate(returnDate);
        borrow.setReturned(false);
        return borrow;
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullBorrow() {
        manager.createBorrow(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithNullCustomerAndBook() {
        Borrow borrow = newBorrow(null, null, LocalDate.now(), LocalDate.MAX);
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithNullCustomer() {
        Borrow borrow = newBorrow(book1, null, LocalDate.now(), LocalDate.MAX);
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithNullBook() {
        Borrow borrow = newBorrow(null, customer1, LocalDate.now(), LocalDate.MAX);
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithBookWhichIsNotInDatabase() {
        customerManager.createCustomer(customer1);

        Borrow borrow = newBorrow(book1, customer1, LocalDate.now(), LocalDate.MAX);
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithCustomerThatIsNotInDatabase() {
        bookManager.createBook(book1);

        Borrow borrow = newBorrow(book1, customer1, LocalDate.now(), LocalDate.MAX);
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithBorrowDateLaterThanReturnDate() {
        bookManager.createBook(book1);

        Borrow borrow = newBorrow(book1, customer1, LocalDate.MAX, LocalDate.MIN);
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteBorrowWithoutArguments() throws Exception {
        Borrow borrow3 = new Borrow();
        manager.deleteBorrow(borrow3);
    }

    @Test
    public void deleteBorrow() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);

        Borrow borrow1 = new Borrow();
        borrow1.setBook(book1);
        borrow1.setCustomer(customer1);
        borrow1.setBorrowDate(LocalDate.now());
        borrow1.setReturnDate(LocalDate.MAX);
        borrow1.setReturned(false);

        Book book2 = new Book();
        book2.setName("Usefull C++");
        book2.setAuthor("Jozo Blcha");
        book2.setIsbn("458-0321358746");
        bookManager.createBook(book2);

        Customer customer2 = new Customer();
        customer2.setName("Filip");
        customer2.setSurname("Tuma");
        customer2.setAddress("Dubodiel");
        customer2.setPhone("+421918948188");
        customerManager.createCustomer(customer1);

        Borrow borrow2 = new Borrow();
        borrow2.setBook(book2);
        borrow2.setCustomer(customer2);
        borrow2.setBorrowDate(LocalDate.now());
        borrow2.setReturnDate(LocalDate.MAX);
        borrow2.setReturned(false);

        manager.createBorrow(borrow1);
        manager.createBorrow(borrow2);

        assertEquals(manager.listAllBorrows().size(), 2);

        manager.deleteBorrow(borrow1);

        assertEquals(manager.listAllBorrows().size(), 1);

        assertEquals(manager.listAllBorrows().get(0), borrow2);
    }

}
