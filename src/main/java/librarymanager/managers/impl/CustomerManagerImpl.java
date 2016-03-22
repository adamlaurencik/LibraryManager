/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanager.managers.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import librarymanager.entities.Customer;
import librarymanager.managers.interfaces.CustomerManager;

/**
 *
 * @author xlauren1
 */
public class CustomerManagerImpl implements CustomerManager {

    private final DataSource dataSource;

    public CustomerManagerImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    @Override
    public void createCustomer(Customer customer) throws FailureException{

        validate(customer);

        if (customer.getId() != null) {
            throw new IllegalArgumentException("customer has already been initialized");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO CUSTOMER (NAME, SURNAME, ADDRESS, PHONE) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, customer.getName());
            st.setString(2, customer.getSurname());
            st.setString(3, customer.getAddress());
            st.setString(4, customer.getPhone());

            int count = st.executeUpdate();
            if (count != 1) {
                throw new FailureException("error in inserting customer, was"
                        + " added " + count + "rows");
            }
            ResultSet rs = st.getGeneratedKeys();
            customer.setId(getKey(rs));

        } catch (SQLException ex) {
            Logger.getLogger(CustomerManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        validate(customer);
        
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customers id is null");
        }
        
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE CUSTOMER SET NAME = ?, SURNAME = ?, ADDRESS = ?, PHONE = ? WHERE ID = ?")) {
            
            st.setString(1, customer.getName());
            st.setString(2, customer.getSurname());
            st.setString(3, customer.getAddress());
            st.setString(4, customer.getPhone());
            st.setLong(5, customer.getId());
            
            int count = st.executeUpdate();

            if (count == 0) {
                throw new NotFoundException("customer was not found in database");
            } else if (count != 1) {
                throw new FailureException("error in updating customer, was updated "
                        + "more than 1 row, was " + count);
            }
        } catch (SQLException ex) {
            throw new FailureException("error when updating book, " + customer);
        }
    }

    @Override
    public void deleteCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM CUSTOMER WHERE ID = ?")) {

            st.setLong(1, customer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new NotFoundException("customer was not found in database");
            } else if (count != 1) {
                throw new FailureException("error when deleting customer,"
                        + "was deleted more customers than 1, was " + count);
            }
        } catch (SQLException ex) {
            throw new FailureException("Error in deleting book " + customer, ex);
        }
    }

    @Override
    public Customer findCustomerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM CUSTOMER WHERE ID = ?")) {

            st.setLong(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Customer customer = resultToCustomer(rs);
                if (rs.next()) {
                    throw new FailureException("More entities with same id");
                }

                return customer;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new FailureException("error in finding customer by id: " + id, ex);
        }
    }

    @Override
    public List<Customer> listAllCustomers() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM CUSTOMER ")) {

            ResultSet rs = st.executeQuery();
            List<Customer> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultToCustomer(rs));
            }

            return result;
        } catch (SQLException ex) {
            throw new FailureException("error when listing all customers ", ex);
        }
    }

    private void validate(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getName() == null || customer.getName().equals("")) {
            throw new IllegalArgumentException("customers name is null or empty string");
        }
        if (customer.getSurname() == null || customer.getSurname().equals("")) {
            throw new IllegalArgumentException("customers surname is null or empty string");
        }
        if (customer.getAddress() == null || customer.getAddress().equals("")) {
            throw new IllegalArgumentException("customers address is null or empty string");
        }
        if (customer.getPhone() == null || customer.getPhone().equals("")) {
            throw new IllegalArgumentException("customers phone is null or empty string");
        }
    }

    private Long getKey(ResultSet rs) throws SQLException, FailureException {
        if (rs.next()) {
            if (rs.getMetaData().getColumnCount() != 1) {
                throw new FailureException(" wrong key fields count: " + rs.getMetaData().getColumnCount());
            }

            Long key = rs.getLong(1);
            if (rs.next()) {
                throw new FailureException("error with generating keys, in process"
                        + " of inserting customer, more than 1 key found");
            }
            return key;
        } else {
            throw new FailureException("error with generating keys, in process"
                    + " of inserting customer, no keys found");
        }
    }

    private Customer resultToCustomer(ResultSet rs) throws SQLException {
    
        Customer customer = new Customer();
       
        customer.setId(rs.getLong("ID"));
        customer.setName(rs.getString("NAME"));
        customer.setSurname(rs.getString("SURNAME"));
        customer.setAddress(rs.getString("ADDRESS"));
        customer.setPhone(rs.getString("PHONE"));
        
        return customer;
    }

}
