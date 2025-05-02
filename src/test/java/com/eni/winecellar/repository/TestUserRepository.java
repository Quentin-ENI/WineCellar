package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Address;
import com.eni.winecellar.bo.customer.Customer;
import com.eni.winecellar.bo.customer.Owner;
import com.eni.winecellar.bo.customer.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class TestUserRepository {

    private static final Logger logger = LoggerFactory.getLogger(TestUserRepository.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    public void initializationDB() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .username("harrisonford@email.fr")
                .password("IndianaJones3")
                .lastname("Ford")
                .firstname("Harrison")
                .authority("CUSTOMER")
                .build());

        users.add(Owner.builder()
                .username("georgelucas@email.fr")
                .password("Réalisateur&Producteur")
                .lastname("Lucas")
                .firstname("George")
                .siret("12345678901234")
                .authority("OWNER")
                .build());

        Address address = Address.builder()
                .street("2 rue Georges Perros")
                .zipCode("29000")
                .city("Quimper")
                .build();

        users.add(Customer.builder()
                .username("natalieportman@email.fr")
                .password("MarsAttacks!")
                .lastname("Portman")
                .firstname("Natalie")
                .authority("CUSTOMER")
                .address(address)
                .build());

        users.forEach(user -> {
            entityManager.persist(user);
        });
    }

    @Test
    public void test_findAll_users() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> logger.info(user.toString()));
        assertEquals(3, users.size());
    }

    @Test
    public void test_findAll_customers() {
        List<Customer> customers = customerRepository.findAll();
        customers.forEach(customer -> logger.info(customer.toString()));
        assertEquals(1, customers.size());
    }

    @Test
    public void test_findAll_owners() {
        List<Owner> owners = ownerRepository.findAll();
        owners.forEach(owner -> logger.info(owner.toString()));
        assertEquals(1, owners.size());
    }

    @Test
    public void test_findByUsername() {
        final User user = userRepository.findByUsername("natalieportman@email.fr");
        assertEquals("Portman", user.getLastname());
        assertEquals("Natalie", user.getFirstname());
    }

    @Test
    public void test_findByUsernameAndPassword() {
        final User user = userRepository.findByUsernameAndPassword("natalieportman@email.fr", "MarsAttacks!");
        assertEquals("Portman", user.getLastname());
        assertEquals("Natalie", user.getFirstname());
    }
}
