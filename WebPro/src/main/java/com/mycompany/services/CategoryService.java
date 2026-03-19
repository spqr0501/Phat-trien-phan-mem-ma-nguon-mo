package com.mycompany.services;

import com.mycompany.models.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    Category findById(int id);
    boolean create(Category category);
    boolean update(Category category);
    boolean delete(int id);
}