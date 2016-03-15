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

/**
 *
 * @author Adam Laurenčík
 */
public class BorrowManagerImplTest {

    private BorrowManagerImpl manager;

    @Before
    public void setUp() {
        manager = new BorrowManagerImpl();
    }

    @Test
    public void createBorrowWithWrongValues() {
        Borrow borrow = new Borrow();
    }

    private static Borrow newBorrow(long id, Book book, Customer customer,
            LocalDate borrowDate, LocalDate returnDate) {
        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setCustomer(customer);
        borrow.setId(id);
        borrow.setBorrowDate(borrowDate);
        borrow.setReturnDate(returnDate);
        borrow.setReturned(false);
        return borrow;
    }
    
}
