/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

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
    
    
    @Test
    public void getAllBooks(){
        
        assertTrue(manager.listAllBooks().isEmpty());
        
        Book book = new Book();
        book.setAuthor("Author");
        book.setName("Testing book");
        book.setAuthor("Author");
        book.setAuthor("Author");
        
        
    }
}
