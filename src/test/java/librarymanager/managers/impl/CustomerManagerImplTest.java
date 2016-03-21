/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import static junit.framework.Assert.assertEquals;
import librarymanager.entities.Customer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Adam Laurenčík
 */
public class CustomerManagerImplTest {
    
    private CustomerManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new CustomerManagerImpl();
    }
    
    @Test
    public void updateCustomer(){
        Customer customer1= new Customer();
        customer1.setName("Peter");
        customer1.setSurname("Lopata");
        customer1.setAddress("Ulice pracovniku 658/17 Brno");
        customer1.setPhone("+421915385002");
        manager.createCustomer(customer1);
        Long customerId = customer1.getId();
        
        customer1.setName("Igor");
        manager.updateCustomer(customer1);
        assertThat(customer1.getName(),is(equalTo("Igor")));
        assertThat(customer1.getSurname(),is(equalTo("Lopata")));
        assertThat(customer1.getAddress(),is(equalTo("Ulice pracovniku 658/17 Brno")));
        assertThat(customer1.getPhone(),is(equalTo("+421915385002")));
        
        customer1.setSurname("Motyka");
        manager.updateCustomer(customer1);
        assertThat(customer1.getName(),is(equalTo("Igor")));
        assertThat(customer1.getSurname(),is(equalTo("Motyka")));
        assertThat(customer1.getAddress(),is(equalTo("Ulice pracovniku 658/17 Brno")));
        assertThat("Customer phone cannot change after surname update",customer1.getPhone(),is(equalTo("+421915385002")));
        
        customer1.setAddress("Praha");
        manager.updateCustomer(customer1);
        assertThat(customer1.getName(),is(equalTo("Igor")));
        assertThat(customer1.getSurname(),is(equalTo("Motyka")));
        assertThat(customer1.getAddress(),is(equalTo("Praha")));
        assertThat(customer1.getPhone(),is(equalTo("+421915385002")));
        
        customer1.setPhone("+420915689552");
        manager.updateCustomer(customer1);    
        assertThat(customer1.getName(),is(equalTo("Igor")));
        assertThat(customer1.getSurname(),is(equalTo("Motyka")));
        assertThat(customer1.getAddress(),is(equalTo("Praha")));
        assertThat(customer1.getPhone(),is(equalTo("+420915689552")));
        
        
        assertEquals(customerId,customer1.getId());
        assertEquals(manager.listAllCustomers().size(),1);
    }
    
    private static Customer newCustomer(long id, String name, String surname,
            String address, String phone) {
        Customer customer = new Customer();
        customer.setAddress(address);
        customer.setId(id);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setSurname(surname);
        return customer;
    }
}
