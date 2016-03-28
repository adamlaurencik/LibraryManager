/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import static junit.framework.Assert.assertEquals;
import librarymanager.entities.Book;
import librarymanager.entities.Borrow;
import librarymanager.entities.Customer;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.sameInstance;
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

            connection.prepareStatement("CREATE TABLE CUSTOMER( "
                    + "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "NAME VARCHAR(25), "
                    + "SURNAME VARCHAR(25), "
                    + "ADDRESS VARCHAR(75), "
                    + "PHONE VARCHAR(50))").executeUpdate();

            connection.prepareStatement("CREATE TABLE BORROW( "
                    + "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "BOOK_ID BIGINT NOT NULL, "
                    + "CUSTOMER_ID BIGINT NOT NULL, "
                    + "BORROW_DATE DATE, "
                    + "RETURN_DATE DATE, "
                    + "RETURNED BOOLEAN, "
                    + "FOREIGN KEY(BOOK_ID) REFERENCES BOOK(ID), "
                    + "FOREIGN KEY(CUSTOMER_ID) REFERENCES CUSTOMER(ID))").executeUpdate();

        }
        bookManager = new BookManagerImpl(dataSource);
        customerManager = new CustomerManagerImpl(dataSource);
        manager = new BorrowManagerImpl(dataSource, bookManager, customerManager);

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
            connection.prepareStatement("DROP TABLE BORROW").executeUpdate();
            connection.prepareStatement("DROP TABLE BOOK").executeUpdate();
            connection.prepareStatement("DROP TABLE CUSTOMER").executeUpdate();
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

    @Test
    public void createBorrow() {

        Borrow borrow = new Borrow();
        bookManager.createBook(book1);
        Long id = book1.getId();
        borrow.setBook(bookManager.findBookById(id));
        customerManager.createCustomer(customer1);
        id = customer1.getId();
        borrow.setCustomer(customerManager.findCustomerById(id));

        borrow.setBorrowDate(LocalDate.now());
        borrow.setReturnDate(borrow.getBorrowDate().plusMonths(1));
        borrow.setReturned(false);

        manager.createBorrow(borrow);
        id = borrow.getId();

        assertNotNull(id);

        Borrow createdBorrow = manager.findBorrowById(id);

        assertEquals(borrow, createdBorrow);
        assertThat(createdBorrow, is(not(sameInstance(borrow))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullBorrow() {
        manager.createBorrow(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithNullCustomerAndBook() {
        Borrow borrow = newBorrow(null, null, LocalDate.ofYearDay(2000, 9),
                LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithNullCustomer() {
        bookManager.createBook(book1);
        Borrow borrow = newBorrow(book1, null, LocalDate.ofYearDay(2000, 9),
                LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithNullBook() {
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(null, customer1, LocalDate.ofYearDay(2000, 9),
                LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithBookWhichIsNotInDatabase() {
        customerManager.createCustomer(customer1);

        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 9),
                LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithCustomerThatIsNotInDatabase() {
        bookManager.createBook(book1);

        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 9),
                LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBorrowWithBorrowDateLaterThanReturnDate() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);

        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 10), LocalDate.ofYearDay(2000, 9));
        manager.createBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteBorrowWithoutArguments() throws Exception {
        Borrow borrow = new Borrow();
        manager.deleteBorrow(borrow);
    }

    @Test
    public void updateBorrow() {

        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = new Borrow();
        borrow.setBook(book1);
        borrow.setCustomer(customer1);
        borrow.setBorrowDate(LocalDate.ofYearDay(2000, 9));
        borrow.setReturnDate(LocalDate.ofYearDay(2000, 10));
        borrow.setReturned(false);

        manager.createBorrow(borrow);
        Long id = borrow.getId();

        Book book = new Book();
        book.setAuthor("Author");
        book.setIsbn("ISBN");
        book.setName("Title");
        bookManager.createBook(book);

        assertNotEquals(manager.findBorrowById(id).getBook(), book);
        borrow.setBook(book);
        manager.updateBorrow(borrow);

        assertEquals(manager.findBorrowById(id).getBook(), book);
        assertEquals(manager.findBorrowById(id).getCustomer(), customer1);
        assertEquals(manager.findBorrowById(id).getBorrowDate(), LocalDate.ofYearDay(2000, 9));
        assertEquals(manager.findBorrowById(id).getReturnDate(), LocalDate.ofYearDay(2000, 10));

        Customer customer = new Customer();
        customer.setAddress("Address");
        customer.setName("Name");
        customer.setPhone("Phone");
        customer.setSurname("Surname");
        customerManager.createCustomer(customer);

        assertNotEquals(manager.findBorrowById(id).getCustomer(), customer);

        borrow.setCustomer(customer);
        manager.updateBorrow(borrow);

        assertEquals(manager.findBorrowById(id).getBook(), book);
        assertEquals(manager.findBorrowById(id).getCustomer(), customer);
        assertEquals(manager.findBorrowById(id).getReturnDate(), LocalDate.ofYearDay(2000, 10));
        assertEquals(manager.findBorrowById(id).getBorrowDate(), LocalDate.ofYearDay(2000, 9));


        borrow.setBorrowDate(LocalDate.ofYearDay(2000, 1) );
        assertNotEquals(manager.findBorrowById(id).getBorrowDate(), LocalDate.ofYearDay(2000, 1));

        manager.updateBorrow(borrow);

        assertEquals(manager.findBorrowById(id).getBook(), book);
        assertEquals(manager.findBorrowById(id).getCustomer(), customer);
        assertEquals(manager.findBorrowById(id).getReturnDate(), LocalDate.ofYearDay(2000, 10));
        assertEquals(manager.findBorrowById(id).getBorrowDate(), LocalDate.ofYearDay(2000, 1));


        borrow.setReturnDate(LocalDate.ofYearDay(2000, 12));
        assertNotEquals(manager.findBorrowById(id).getReturnDate(),LocalDate.ofYearDay(2000, 12));

        manager.updateBorrow(borrow);

        assertEquals(manager.findBorrowById(id).getBook(), book);
        assertEquals(manager.findBorrowById(id).getCustomer(), customer);
        assertEquals(manager.findBorrowById(id).getReturnDate(), LocalDate.ofYearDay(2000, 12));
        assertEquals(manager.findBorrowById(id).getBorrowDate(), LocalDate.ofYearDay(2000, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowWithNull() throws Exception {
        manager.updateBorrow(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsIdWithNull() throws Exception {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setId(null);
        manager.updateBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsBookWithNull() throws Exception {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setBook(null);
        manager.updateBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsCustomerWithNull() throws Exception {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setCustomer(null);
        manager.updateBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsBorrowDateWithNull() throws Exception {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setBorrowDate(null);
        manager.updateBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsReturnDateWithNull() throws Exception {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setReturnDate(null);
        manager.updateBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsWithBookNotInDatabase() throws Exception {
        Book book = new Book();
        book.setAuthor("author");
        book.setIsbn("isbn");
        book.setName("name");

        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setBook(book);
        manager.updateBorrow(borrow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBorrowsWithCustomerNotInDatabase() throws Exception {
        Customer customer = new Customer();
        customer.setAddress("address");
        customer.setName("name");
        customer.setPhone("phone");
        customer.setSurname("surname");

        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);

        borrow.setCustomer(customer);
        manager.updateBorrow(borrow);
    }

    @Test
    public void deleteBorrow() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);

        Borrow borrow1 = new Borrow();
        borrow1.setBook(book1);
        borrow1.setCustomer(customer1);
        borrow1.setBorrowDate(LocalDate.now());
        borrow1.setReturnDate(LocalDate.now().plusMonths(1));
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
        customerManager.createCustomer(customer2);

        Borrow borrow2 = new Borrow();
        borrow2.setBook(book2);
        borrow2.setCustomer(customer2);
        borrow2.setBorrowDate(LocalDate.now());
        borrow2.setReturnDate(LocalDate.now().plusMonths(1));
        borrow2.setReturned(false);

        manager.createBorrow(borrow1);
        manager.createBorrow(borrow2);

        assertEquals(manager.listAllBorrows().size(), 2);

        manager.deleteBorrow(borrow1);

        assertEquals(manager.listAllBorrows().size(), 1);
        assertEquals(manager.listAllBorrows().get(0), borrow2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteBorrowWhichIsNotInDatabase() throws Exception {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));

        manager.deleteBorrow(borrow);
    }

    @Test
    public void findBorrowById() {
        assertNull(manager.findBorrowById(new Long(1)));

        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        
        manager.createBorrow(borrow);

        Borrow borrowFromDb = manager.findBorrowById(borrow.getId());
        assertEquals(borrowFromDb, borrow);
    }

    @Test
    public void listAllBorrows() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
        
        assertTrue(manager.listAllBorrows().size() == 1);
        assertEquals(borrow, manager.listAllBorrows().get(0));

        Book book = new Book();
        book.setAuthor("author");
        book.setIsbn("isbn");
        book.setName("name");

        Customer customer = new Customer();
        customer.setAddress("address");
        customer.setName("name");
        customer.setPhone("phone");
        customer.setSurname("surname");

        bookManager.createBook(book);
        customerManager.createCustomer(customer);
        Borrow borrow1 = newBorrow(book, customer, LocalDate.ofYearDay(2000, 2), LocalDate.ofYearDay(2000, 11));
        manager.createBorrow(borrow1);
        assertTrue(manager.listAllBorrows().size() == 2);

        List<Borrow> expected = new ArrayList<>();
        expected.add(borrow);
        expected.add(borrow1);
        expected.sort(idComparator);

        List<Borrow> returned = manager.listAllBorrows();
        returned.sort(idComparator);

        assertEquals(expected, returned);
    }

    @Test
    public void listBorrowedBooks() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        borrow.setReturned(true);
        manager.createBorrow(borrow);
        assertEquals(manager.listBorrowedBooks().size(), 0);

        borrow.setReturned(false);
        manager.updateBorrow(borrow);
        assertEquals(manager.listBorrowedBooks().size(), 1);
        assertEquals(book1, manager.listBorrowedBooks().get(0));

        Book book = new Book();
        book.setAuthor("author");
        book.setIsbn("isbn");
        book.setName("name");

        Customer customer = new Customer();
        customer.setAddress("address");
        customer.setName("name");
        customer.setPhone("phone");
        customer.setSurname("surname");
        bookManager.createBook(book);
        customerManager.createCustomer(customer);
        
        Borrow borrow1 = newBorrow(book, customer, LocalDate.ofYearDay(2000, 2), LocalDate.ofYearDay(2000, 12));
        manager.createBorrow(borrow1);
        assertEquals(manager.listBorrowedBooks().size(), 2);

        List<Book> expected = new ArrayList<>();
        expected.add(book);
        expected.add(book1);
        expected.sort(idComparatorForBooks);

        List<Book> returned = manager.listBorrowedBooks();
        returned.sort(idComparatorForBooks);

        assertEquals(expected, returned);
    }

    @Test
    public void findBorrowForBook() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Long id = book1.getId();
        
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
        
        borrow.setReturned(true);
        assertEquals(manager.findBorrowForBook(book1).size(), 1);
        
        Customer customer = new Customer();
        customer.setAddress("address");
        customer.setName("name");
        customer.setPhone("phone");
        customer.setSurname("surname");

        customerManager.createCustomer(customer);
        Borrow borrow1 = newBorrow(book1, customer, LocalDate.ofYearDay(2000, 2), LocalDate.ofYearDay(2000, 12));
        manager.createBorrow(borrow1);
        borrow1.setReturned(true);
        
        assertEquals(manager.findBorrowForBook(book1).size(), 2);

        List<Borrow> expected = new ArrayList<>();
        expected.add(borrow);
        expected.add(borrow1);
        
        expected.sort(idComparator);

        List<Borrow> returned = manager.findBorrowForBook(book1);
        returned.sort(idComparator);

        assertEquals(expected, returned);
    }
    
    @Test
    public void findBorrowForCustomer() {
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
        Long id = customer1.getId();
        
        Borrow borrow = newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 10));
        manager.createBorrow(borrow);
        
        borrow.setReturned(true);
        assertEquals(manager.findBorrowForCustomer(customer1).size(), 1);
        
        
        Book book = new Book();
        book.setAuthor("author");
        book.setIsbn("isbn");
        book.setName("name");

        bookManager.createBook(book);
        Borrow borrow1 = newBorrow(book, customer1, LocalDate.ofYearDay(2000, 2), LocalDate.ofYearDay(2000, 12));
        manager.createBorrow(borrow1);
        borrow1.setReturned(true);
        
        assertEquals(manager.findBorrowForCustomer(customer1).size(), 2);

        List<Borrow> expected = new ArrayList<>();
        expected.add(borrow);
        expected.add(borrow1);
        
        expected.sort(idComparator);

        List<Borrow> returned = manager.findBorrowForCustomer(customer1);
        returned.sort(idComparator);
       
        assertEquals(expected, returned);
    }
    
    @Test
    public void isBorrowed(){
        bookManager.createBook(book1);
        customerManager.createCustomer(customer1);
 
        assertFalse(manager.isBorrowed(book1));
        
        Borrow borrow=newBorrow(book1, customer1, LocalDate.ofYearDay(2000, 1), LocalDate.ofYearDay(2000, 31));
        manager.createBorrow(borrow);
        
        assertTrue(manager.isBorrowed(book1));
        
        borrow.setReturned(true);
        
        manager.updateBorrow(borrow);
        
        assertFalse(manager.isBorrowed(book1));
        
        
    }
    
    private static Comparator<Borrow> idComparator = new Comparator<Borrow>() {
        @Override
        public int compare(Borrow o1, Borrow o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    };

    private static Comparator<Book> idComparatorForBooks = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    };

}