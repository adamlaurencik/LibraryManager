/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;


import java.time.LocalDate;
import librarymanager.entities.Book;
import librarymanager.entities.Borrow;
import librarymanager.entities.Customer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Adam Laurenčík
 */
public class BorrowManagerImplTest {
    

    private BorrowManagerImpl manager;
    private BookManagerImpl bookManager;
    private CustomerManagerImpl customerManager;
    
    @Before
    public void setUp() {
        manager= new BorrowManagerImpl();
        bookManager= new BookManagerImpl();
        customerManager= new CustomerManagerImpl();
    }
    
      @Test(expected = IllegalArgumentException.class)
    public void testDeleteBorrowWithNull() throws Exception {
        manager.deleteBorrow(null);
    }
    
      @Test(expected = IllegalArgumentException.class)
    public void testDeleteBorrowWithoutArguments() throws Exception {
        Borrow borrow3= new Borrow();
        manager.deleteBorrow(borrow3);
    }
   
    
     @Test
    public void deleteBorrow(){
        Book book1 = new Book();
        book1.setName("Effective Java");
        book1.setAuthor("Joshua Bloch");
        book1.setIsbn("978-0321356680");
        bookManager.createBook(book1);
        
        Customer customer1 = new Customer();
        customer1.setName("Milan");
        customer1.setSurname("Pazitka");
        customer1.setAddress("Uherske Hradiste");
        customer1.setPhone("+421915538112");
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
        
         assertEquals(manager.listAllBorrows().size(),1);
         
         assertEquals(manager.listAllBorrows().get(0),borrow2);
    }
    
    
    
    
    
    
 
    
}
