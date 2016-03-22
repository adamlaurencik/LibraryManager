/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.interfaces;

import java.util.List;
import librarymanager.entities.Customer;

/**
 *
 * @author xlauren1
 */
public interface CustomerManager {
    
  public void createCustomer(Customer customer);
    public void updateCustomer(Customer customer);
    public void deleteCustomer(Customer customer);
    public Customer findCustomerById(Long id);
    public List<Customer> listAllCustomers();
}
