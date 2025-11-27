package com.barbershop.config;

import com.barbershop.entity.Account;
import com.barbershop.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private AccountRepository repo;

    @Override
    public void run(String... args) throws Exception {

        if (repo.findByUsername("admin") == null) {
            Account acc = new Account();
            acc.setUsername("admin");
            acc.setPassword("123");
            acc.setRole("ROLE_ADMIN");
            repo.save(acc);

            System.out.println(">>> Admin account created: admin / 123");
        }
    }
}
