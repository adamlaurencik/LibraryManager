/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

/**
 *
 * @author Administrátor
 */
public class NotFoundException extends RuntimeException{
    
    public NotFoundException() {
        super();
    }
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(Throwable ex) {
        super(ex);
    }
    
    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
