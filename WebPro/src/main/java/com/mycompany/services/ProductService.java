// src/main/java/com/mycompany/services/ProductService.java
package com.mycompany.services;

import com.mycompany.models.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAll();

    List<Product> findByXuhuong(Integer xuhuong);

    List<Product> findByPhobien(Integer phobien);

    Product findByMahh(String mahh); // <-- đổi thành findByMahh

    boolean create(Product product);

    boolean update(Product product);

    boolean delete(String mahh);
}