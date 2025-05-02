package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Address;
import com.eni.winecellar.bo.customer.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TestCustomerRepository {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void test_saveCustomer() {
        Address address = Address.builder()
                .street("2 rue Georges Perros")
                .zipCode("29000")
                .city("Quimper")
                .build();

        Customer customer = Customer.builder()
                .username("JohnDoe")
                .password("password")
                .firstname("John")
                .lastname("Doe")
                .authority("CUSTOMER")
                .address(address)
                .build();

        final Customer savedCustomer = customerRepository.save(customer);
        final Customer customerToTest = this.entityManager.find(Customer.class, "JohnDoe");
        assertEquals(savedCustomer.getUsername(), customerToTest.getUsername());
        assertNotNull(customerToTest.getAddress());
        assertNotNull(customerToTest.getAddress().getId());
    }

    @Test
    public void test_removeCustomer() {
        Address address = Address.builder()
                .street("2 rue Georges Perros")
                .zipCode("29000")
                .city("Quimper")
                .build();

        Customer customer = Customer.builder()
                .username("JohnDoe")
                .password("password")
                .firstname("John")
                .lastname("Doe")
                .authority("CUSTOMER")
                .address(address)
                .build();

        final Customer savedCustomer = customerRepository.save(customer);
        final int idAddress = savedCustomer.getAddress().getId();
        customerRepository.delete(savedCustomer);
        final Customer customerToTest = this.entityManager.find(Customer.class, "JohnDoe");
        final Address addressToTest = this.entityManager.find(Address.class, idAddress);
        assertNotNull(savedCustomer);
        assertNull(customerToTest);
        assertNull(addressToTest);
    }

    @Test
    public void test_updateCustomer() {
        Address address = Address.builder()
                .street("2 rue Georges Perros")
                .zipCode("29000")
                .city("Brest")
                .build();

        Customer customer = Customer.builder()
                .username("JohnDoe")
                .password("password")
                .firstname("John")
                .lastname("Doe")
                .authority("CUSTOMER")
                .address(address)
                .build();

        final Customer savedCustomer = customerRepository.save(customer);
        assertEquals("John", savedCustomer.getFirstname());
        customer.setFirstname("Jane");
        customer.getAddress().setCity("Quimper");
        customerRepository.save(customer);
        final Customer customerToTest = this.entityManager.find(Customer.class, "JohnDoe");
        assertEquals("Jane", customerToTest.getFirstname());
        assertEquals("Quimper", customerToTest.getAddress().getCity());
    }
}
