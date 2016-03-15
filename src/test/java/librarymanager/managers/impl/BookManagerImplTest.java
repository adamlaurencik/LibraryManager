/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import static junit.framework.Assert.*;
import librarymanager.entities.Book;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Adam Laurenčík
 */
public class BookManagerImplTest {

    private BookManagerImpl manager;

    @Before
    public void setUp() {
        manager = new BookManagerImpl();
    }
    // Lauro spravil list all book 
    
    //Marek
    @Test
    public void deleteBook() {
        assertTrue(manager.listAllBooks().isEmpty());

        Book first = newBook(1, "book1", "someone", "isbn");
        Book second = newBook(2, "book2", "someone", "isbn2");

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

    private static Book newBook(long id, String name, String author, String isbn) {
        Book book = new Book();
        book.setAuthor(author);
        book.setId(id);
        book.setIsbn(isbn);
        book.setName(name);
        return book;
    }
}
