/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.interfaces;

import java.util.List;
import librarymanager.entities.Book;

/**
 *
 * @author xlauren1
 */
public interface BookManager {
    
    public void createBook(Book book);
    public void updateBook(Book book);
    public void deleteBook(Book book);
    public Book findBookById(long id);
    public List<Book> listAllBooks();
}
