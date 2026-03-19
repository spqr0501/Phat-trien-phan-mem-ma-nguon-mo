// ProductServiceImpl.java - FIXED with comprehensive validation
package com.mycompany.services;

import com.mycompany.models.Product;
import com.mycompany.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findByXuhuong(Integer xuhuong) {
        return productRepository.findByXuhuong(xuhuong);
    }

    @Override
    public List<Product> findByPhobien(Integer phobien) {
        return productRepository.findByPhobien(phobien);
    }

    @Override
    public Product findByMahh(String mahh) {
        return productRepository.findById(mahh).orElse(null);
    }

    @Override
    public boolean create(Product product) {
        try {
            // Validation: Kiểm tra mã hàng không được rỗng
            if (product.getMahh() == null || product.getMahh().trim().isEmpty()) {
                System.err.println("Lỗi: Mã hàng hóa không được để trống!");
                return false;
            }

            // Validation: Kiểm tra tên hàng không được rỗng
            if (product.getTenhh() == null || product.getTenhh().trim().isEmpty()) {
                System.err.println("Lỗi: Tên hàng hóa không được để trống!");
                return false;
            }

            // Validation: Kiểm tra đơn giá phải > 0
            if (product.getDongia() <= 0) {
                System.err.println("Lỗi: Đơn giá phải lớn hơn 0!");
                return false;
            }

            // Kiểm tra mã hàng đã tồn tại chưa
            if (productRepository.existsById(product.getMahh())) {
                System.err.println("Lỗi: Mã hàng " + product.getMahh() + " đã tồn tại!");
                return false;
            }

            productRepository.save(product);
            System.out.println("Thêm sản phẩm thành công: " + product.getTenhh());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        try {
            // Validation: Kiểm tra tên hàng không được rỗng
            if (product.getTenhh() == null || product.getTenhh().trim().isEmpty()) {
                System.err.println("Lỗi: Tên hàng hóa không được để trống!");
                return false;
            }

            // Validation: Kiểm tra đơn giá phải > 0
            if (product.getDongia() <= 0) {
                System.err.println("Lỗi: Đơn giá phải lớn hơn 0!");
                return false;
            }

            // Kiểm tra product có tồn tại không
            if (!productRepository.existsById(product.getMahh())) {
                System.err.println("Lỗi: Không tìm thấy sản phẩm với mã " + product.getMahh());
                return false;
            }

            productRepository.save(product);
            System.out.println("Cập nhật sản phẩm thành công: " + product.getTenhh());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String mahh) {
        try {
            if (!productRepository.existsById(mahh)) {
                System.err.println("Lỗi: Không tìm thấy sản phẩm với mã " + mahh);
                return false;
            }
            productRepository.deleteById(mahh);
            System.out.println("Xóa sản phẩm thành công: mã " + mahh);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}