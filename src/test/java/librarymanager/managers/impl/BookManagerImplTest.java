/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import librarymanager.entities.Book;
import static org.junit.Assert.assertEquals;
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
        assertEquals(expected,result);
        
        manager.deleteBook(book1);
        assertEquals(manager.listAllBooks().size(),2);
    }
    
    

    private static Comparator<Book> idComparator = new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    };
}
