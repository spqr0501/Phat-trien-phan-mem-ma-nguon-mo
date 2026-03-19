package com.mycompany.services;

import com.mycompany.models.Category;
import com.mycompany.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public boolean create(Category category) {
        try {
            // Validation: Kiểm tra tên loại không được rỗng
            if (category.getTenloai() == null || category.getTenloai().trim().isEmpty()) {
                System.err.println("Lỗi: Tên loại hàng không được để trống!");
                return false;
            }

            // Kiểm tra mã loại đã tồn tại chưa (nếu có maloai > 0)
            if (category.getMaloai() > 0 && categoryRepository.existsById(category.getMaloai())) {
                System.err.println("Lỗi: Mã loại " + category.getMaloai() + " đã tồn tại!");
                return false;
            }

            categoryRepository.save(category);
            System.out.println("Thêm loại hàng thành công: " + category.getTenloai());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm loại hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Category category) {
        try {
            // Validation: Kiểm tra tên loại không được rỗng
            if (category.getTenloai() == null || category.getTenloai().trim().isEmpty()) {
                System.err.println("Lỗi: Tên loại hàng không được để trống!");
                return false;
            }

            // Kiểm tra category có tồn tại không
            if (!categoryRepository.existsById(category.getMaloai())) {
                System.err.println("Lỗi: Không tìm thấy loại hàng với mã " + category.getMaloai());
                return false;
            }

            categoryRepository.save(category);
            System.out.println("Cập nhật loại hàng thành công: " + category.getTenloai());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật loại hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            if (!categoryRepository.existsById(id)) {
                System.err.println("Lỗi: Không tìm thấy loại hàng với mã " + id);
                return false;
            }
            categoryRepository.deleteById(id);
            System.out.println("Xóa loại hàng thành công: mã " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa loại hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}