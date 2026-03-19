package com.mycompany.repository;

import com.mycompany.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Tìm đơn hàng theo mã khách hàng
    List<Order> findByMakh(String makh);

    // Tìm đơn hàng theo trạng thái
    List<Order> findByTrangthai(String trangthai);

    // Tìm đơn hàng theo email
    List<Order> findByEmail(String email);
}
