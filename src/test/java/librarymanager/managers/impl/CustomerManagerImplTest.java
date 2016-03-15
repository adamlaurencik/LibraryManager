/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import librarymanager.entities.Customer;
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
    public void updateCustomer() {

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
