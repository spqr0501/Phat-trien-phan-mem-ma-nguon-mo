package com.mycompany.services;

import com.mycompany.models.Customer;
import com.mycompany.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(String id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public boolean create(Customer customer) {
        try {
            // Validation: Kiểm tra mã khách hàng không được rỗng
            if (customer.getMakh() == null || customer.getMakh().trim().isEmpty()) {
                System.err.println("Lỗi: Mã khách hàng không được để trống!");
                return false;
            }

            // Validation: Kiểm tra tên khách hàng không được rỗng
            if (customer.getTenkh() == null || customer.getTenkh().trim().isEmpty()) {
                System.err.println("Lỗi: Tên khách hàng không được để trống!");
                return false;
            }

            // Kiểm tra mã khách hàng đã tồn tại chưa
            if (customerRepository.existsById(customer.getMakh())) {
                System.err.println("Lỗi: Mã khách hàng " + customer.getMakh() + " đã tồn tại!");
                return false;
            }

            customerRepository.save(customer);
            System.out.println("Thêm khách hàng thành công: " + customer.getTenkh());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Customer customer) {
        try {
            // Validation: Kiểm tra tên khách hàng không được rỗng
            if (customer.getTenkh() == null || customer.getTenkh().trim().isEmpty()) {
                System.err.println("Lỗi: Tên khách hàng không được để trống!");
                return false;
            }

            // Kiểm tra customer có tồn tại không
            if (!customerRepository.existsById(customer.getMakh())) {
                System.err.println("Lỗi: Không tìm thấy khách hàng với mã " + customer.getMakh());
                return false;
            }

            customerRepository.save(customer);
            System.out.println("Cập nhật khách hàng thành công: " + customer.getTenkh());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            if (!customerRepository.existsById(id)) {
                System.err.println("Lỗi: Không tìm thấy khách hàng với mã " + id);
                return false;
            }
            customerRepository.deleteById(id);
            System.out.println("Xóa khách hàng thành công: mã " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}