package com.mycompany.services;

import com.mycompany.models.Menu;
import com.mycompany.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    // Get all menus
    public List<Menu> getAllMenus() {
        return menuRepository.findAllByOrderByPositionAsc();
    }

    // Get menu by ID
    public Optional<Menu> getMenuById(Integer id) {
        return menuRepository.findById(id);
    }

    // Get root menus (no parent)
    public List<Menu> getRootMenus() {
        return menuRepository.findByParentIdIsNullOrderByPositionAsc();
    }

    // Get submenus by parent ID
    public List<Menu> getSubMenus(Integer parentId) {
        return menuRepository.findByParentIdOrderByPositionAsc(parentId);
    }

    // Get active menus only
    public List<Menu> getActiveMenus() {
        return menuRepository.findByStatusOrderByPositionAsc("ACTIVE");
    }

    // Create new menu
    public Menu createMenu(Menu menu) {
        if (menu.getMenuName() == null || menu.getMenuName().trim().isEmpty()) {
            throw new IllegalArgumentException("Menu name cannot be empty");
        }
        if (menu.getPosition() == null) {
            menu.setPosition(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus("ACTIVE");
        }
        return menuRepository.save(menu);
    }

    // Update existing menu
    public Menu updateMenu(Integer id, Menu updatedMenu) {
        Optional<Menu> existingMenu = menuRepository.findById(id);
        if (existingMenu.isEmpty()) {
            throw new IllegalArgumentException("Menu not found with ID: " + id);
        }

        Menu menu = existingMenu.get();
        menu.setMenuName(updatedMenu.getMenuName());
        menu.setParentId(updatedMenu.getParentId());
        menu.setPosition(updatedMenu.getPosition());
        menu.setStatus(updatedMenu.getStatus());
        if (updatedMenu.getUrl() != null) {
            menu.setUrl(updatedMenu.getUrl());
        }

        return menuRepository.save(menu);
    }

    // Delete menu
    public void deleteMenu(Integer id) {
        // Check if menu has children
        long childCount = menuRepository.countByParentId(id);
        if (childCount > 0) {
            throw new IllegalArgumentException("Cannot delete menu with submenus. Delete submenus first.");
        }
        menuRepository.deleteById(id);
    }

    // Check if menu exists
    public boolean menuExists(Integer id) {
        return menuRepository.existsById(id);
    }
}
