package com.eni.winecellar.repository;

import com.eni.winecellar.bo.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class CustomerRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void test_save() {
        Customer customer = Customer.builder()
                .username("JohnDoe")
                .password("password")
                .firstname("John")
                .lastname("Doe")
                .build();

        final Customer customerToTest = customerRepository.save(customer);
        System.out.println(customerToTest);

    }
}
