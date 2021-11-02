package com.zweaver.statistics.controller;

import java.util.List;

import com.zweaver.statistics.entity.Customer;
import com.zweaver.statistics.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class CustomerController {
    
    @Autowired
    private CustomerRepository customerRepository;

    // get customers
    @GetMapping("customers")
    public List<Customer> getAllCustomers() {
        return this.customerRepository.findAll();
    }

    // add customer
    @PostMapping("customers")
    public Customer addNewCustomer(@RequestBody Customer newCustomer) {
        return this.customerRepository.save(newCustomer);
    }
}
