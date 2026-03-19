package com.mycompany.repository;

import com.mycompany.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByXuhuong(Integer xuhuong);

    List<Product> findByPhobien(Integer phobien);

    List<Product> findByLoaihh(int loaihh);

    @Query("SELECT p FROM Product p WHERE LOWER(p.tenhh) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // Stock-related queries for admin dashboard
    Long countBySoluongLessThan(Integer threshold);

    Long countBySoluong(Integer quantity);

    List<Product> findBySoluongLessThanOrderBySoluongAsc(Integer threshold);
}