package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
//    @Query("SELECT u FROM User u WHERE u.username=:username")
//    User findByUsername(@Param("username") String username);

    User findByUsername(String username);

//    @Query("SELECT u FROM User u WHERE u.username=:username AND u.password=:password")
//    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    User findByUsernameAndPassword(String username, String password);
}
