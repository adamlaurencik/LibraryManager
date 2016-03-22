/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.interfaces;

import java.util.List;
import librarymanager.entities.Book;
import librarymanager.managers.impl.FailureException;

/**
 *
 * @author xlauren1
 */
public interface BookManager {
    
    public void createBook(Book book) throws FailureException;
    public void updateBook(Book book) throws FailureException;
    public void deleteBook(Book book) throws FailureException;
    public Book findBookById(Long id) throws FailureException;
    public List<Book> listAllBooks() throws FailureException;
}
