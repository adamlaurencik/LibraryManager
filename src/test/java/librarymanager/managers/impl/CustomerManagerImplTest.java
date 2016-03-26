/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import librarymanager.entities.Customer;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import org.junit.After;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Adam Laurenčík
 */
public class CustomerManagerImplTest {
    
    private CustomerManagerImpl manager;
    private Customer customer; 
    private DataSource dataSource;
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE CUSTOMER( "
                    + "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "NAME VARCHAR(50), "
                    + "SURNAME VARCHAR(50), "
                    + "ADDRESS VARCHAR(50), "
                    + "PHONE VARCHAR(50))").executeUpdate();
        }
        manager = new CustomerManagerImpl(dataSource);
        customer = newCustomer("Name", "Surname", "address", "+421...");
    }
    
    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DROP TABLE CUSTOMER").executeUpdate();
        }
    }
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:customerManagerImpl-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Test
    public void createCustomer(){
        manager.createCustomer(customer);
        Long id = customer.getId();
        
        assertNotNull(id);

        Customer createdCustomer = manager.findCustomerById(id);

        assertEquals(customer, createdCustomer);
        assertThat(createdCustomer, is(not(sameInstance(customer))));
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
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCustomerWithNull() throws Exception {
        manager.updateCustomer(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCustomersIdWithNull() throws Exception {
        manager.createCustomer(customer);
        
        customer.setId(null);
        manager.updateCustomer(customer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCustomersNameWithNull() throws Exception {
        manager.createCustomer(customer);
        
        customer.setName(null);
        manager.updateCustomer(customer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCustomersSurnameWithNull() throws Exception {
        manager.createCustomer(customer);
        
        customer.setSurname(null);
        manager.updateCustomer(customer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCustomersAddressWithNull() throws Exception {
        manager.createCustomer(customer);
        
        customer.setAddress(null);
        manager.updateCustomer(customer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCustomersPhoneWithNull() throws Exception {
        manager.createCustomer(customer);
        customer.setPhone(null);
        manager.updateCustomer(customer);
    }
    
    @Test
    public void deleteCustomer(){
       Customer customer1 = newCustomer("Jozo", "Raz", "Brno", "000");
       manager.createCustomer(customer1);
       
       Customer customer2 = newCustomer("Milan", "Fero", "Praha", "0320");
       manager.createCustomer(customer2);
       
       manager.deleteCustomer(customer1);
       
       assertTrue(manager.listAllCustomers().size()==1);
       
       assertEquals(customer2, manager.listAllCustomers().get(0));
       
       manager.deleteCustomer(customer2);
       
       assertTrue(manager.listAllCustomers().isEmpty());
       
    }
    
    @Test
    public void listAllCustomers(){
       Customer customer1 = newCustomer("Jozo", "Raz", "Brno", "000");
       manager.createCustomer(customer1);
       
       assertTrue(manager.listAllCustomers().size()==1);
       
       assertEquals(customer1, manager.listAllCustomers().get(0));
       
       Customer customer2 = newCustomer("Milan", "Fero", "Praha", "0320");
       manager.createCustomer(customer2);
       
       assertTrue(manager.listAllCustomers().size()==2);
       
       List<Customer> expected=new ArrayList<>();
       expected.add(customer1);
       expected.add(customer2);
        expected.sort(idComparator);
       
      List<Customer> returned = manager.listAllCustomers();
      returned.sort(idComparator);
      
      assertEquals(expected, returned);
    }
    
    @Test
    public void findCustomerById(){
        assertNull(manager.findCustomerById(new Long(1)));
        
        Customer customer = newCustomer("customer", "surname", "Praha","+421");
        manager.createCustomer(customer);
        
        Customer customerFromDb=manager.findCustomerById(customer.getId());
        assertEquals(customerFromDb, customer);
    }
    
  private static Comparator<Customer> idComparator = new Comparator<Customer>() {
        @Override
        public int compare(Customer o1, Customer o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    };
    
    private static Customer newCustomer(String name, String surname,
            String address, String phone) {
        Customer customer = new Customer();
        customer.setAddress(address);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setSurname(surname);
        return customer;
    }
}
