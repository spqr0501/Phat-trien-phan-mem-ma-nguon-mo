package com.mycompany.services;

import com.mycompany.models.Customer;
import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
    Customer findById(String id);
    boolean create(Customer customer);
    boolean update(Customer customer);
    boolean delete(String id);
}