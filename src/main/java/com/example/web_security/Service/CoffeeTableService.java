package com.example.web_security.service;

import com.example.web_security.model.Coffee;
import com.example.web_security.Repo.CoffeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoffeeTableService {

    @Autowired
    private CoffeRepository tableRepository;

    public Coffee createTable(Coffee table) {
        return tableRepository.save(table);
    }
}
