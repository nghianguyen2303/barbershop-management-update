package com.barbershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.barbershop.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByUsername(String username);

    boolean existsByUsername(String username);

    Account findByUsernameAndPassword(String username, String password);
}

