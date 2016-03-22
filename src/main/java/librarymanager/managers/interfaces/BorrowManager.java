/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.interfaces;

import java.util.List;
import librarymanager.entities.Book;
import librarymanager.entities.Borrow;
import librarymanager.entities.Customer;

/**
 *
 * @author xlauren1
 */
public interface BorrowManager {
    
    public void createBorrow(Borrow borrow);
    public void updateBorrow(Borrow borrow);
    public void deleteBorrow(Borrow borrow);
    public Borrow findBorrowById(Long id);
    public List<Borrow> findBorrowForCustomer(Customer customer);
    public List<Borrow> findBorrowForBook(Book book);
    public List<Book> listBorrowedBooks();
    public List<Borrow> listAllBorrows();
    public boolean isBorrowed(Book book);
    
}
