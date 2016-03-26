/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import static junit.framework.Assert.*;
import librarymanager.entities.Book;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Adam Laurenčík
 */
public class BookManagerImplTest {

    private BookManagerImpl manager;
    private DataSource dataSource;

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
        manager = new BookManagerImpl(dataSource);
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

    @Test
    public void createBook() {
        Book book = newBook("book1", "someone", "isbn");
        manager.createBook(book);
        Long id = book.getId();

        assertNotNull(id);

        Book createdBook = manager.findBookById(id);

        assertEquals(book, createdBook);
        assertThat(createdBook, is(not(sameInstance(book))));

    }

    @Test
    public void listAllBooks() {
        assertTrue(manager.listAllBooks().isEmpty());
        Book book1 = new Book();
        book1.setAuthor("Joshua Bloch");
        book1.setName("Effective Java");
        book1.setIsbn("978-0321356680");

        Book book2 = new Book();
        book2.setAuthor("Johnny Mecoch");
        book2.setName("Skola tanca");
        book2.setIsbn("238-8321565103");

        manager.createBook(book1);
        assertTrue(manager.listAllBooks().size() == 1);

        manager.createBook(book2);
        assertTrue(manager.listAllBooks().size() == 2);

        List<Book> expected = new ArrayList<>();
        expected.add(book1);
        expected.add(book2);

        List<Book> result = manager.listAllBooks();
        Collections.sort(expected, idComparator);
        Collections.sort(result, idComparator);
        assertEquals(expected, result);

        manager.deleteBook(book1);
        assertEquals(manager.listAllBooks().size(), 1);
    }

    @Test
    public void deleteBook() {
        assertTrue(manager.listAllBooks().isEmpty());

        Book first = newBook("book1", "someone", "isbn");
        Book second = newBook("book2", "someone", "isbn2");

        manager.createBook(first);
        manager.createBook(second);

        assertEquals(manager.listAllBooks().size(), 2);
        assertNotNull(manager.findBookById(first.getId()));
        assertNotNull(manager.findBookById(second.getId()));

        manager.deleteBook(second);

        assertEquals(manager.listAllBooks().size(), 1);
        assertNull(manager.findBookById(second.getId()));
        assertNotNull(manager.findBookById(first.getId()));
    }
    
    
    @Test
    public void updateBook(){
        assertTrue(manager.listAllBooks().isEmpty());
        
        Book book=newBook("book", "someone", "isb");
        manager.createBook(book);
        assertTrue(manager.listAllBooks().size()==1);
        book.setName("book2");
        manager.updateBook(book);        
        Book bookFromDb=manager.findBookById(book.getId());
        
        assertEquals(bookFromDb, book);
        
    }
    
   @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNoId() throws Exception {
        Book noIdBook= newBook("Book", "author", "isbn");
        manager.updateBook(noIdBook);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNull() throws Exception {
        manager.updateBook(null);
    }
    
    
    @Test
    public void findBookById(){
        assertNull(manager.findBookById(new Long(1)));
        
        Book book = newBook("book", "author", "isbn");
        manager.createBook(book);
        
        Book bookFromDb=manager.findBookById(book.getId());
        assertEquals(bookFromDb, book);
    }

    private static Book newBook(String name, String author, String isbn) {
        Book book = new Book();
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setName(name);
        return book;
    }

    private static Comparator<Book> idComparator = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    };
}
