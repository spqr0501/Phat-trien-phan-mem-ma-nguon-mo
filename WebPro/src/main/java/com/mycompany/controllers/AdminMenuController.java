package com.mycompany.controllers;

import com.mycompany.models.Menu;
import com.mycompany.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/menu")
public class AdminMenuController {

    @Autowired
    private MenuService menuService;

    // View menu list
    @GetMapping("")
    public String menuList(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        List<Menu> rootMenus = menuService.getRootMenus();

        model.addAttribute("menus", menus);
        model.addAttribute("rootMenus", rootMenus);
        return "admin/menu";
    }

    // Add menu page
    @GetMapping("/add")
    public String addMenuPage(Model model) {
        List<Menu> parentMenus = menuService.getRootMenus();
        model.addAttribute("parentMenus", parentMenus);
        return "admin/add-menu";
    }

    // Save new menu
    @PostMapping("/add")
    public String saveMenu(@RequestParam String menuName,
            @RequestParam(required = false) Integer parentMenu,
            @RequestParam int position,
            @RequestParam String status,
            @RequestParam(required = false) String url,
            RedirectAttributes redirectAttributes) {
        try {
            Menu menu = new Menu();
            menu.setMenuName(menuName);
            menu.setParentId(parentMenu);
            menu.setPosition(position);
            menu.setStatus(status);
            menu.setUrl(url);

            menuService.createMenu(menu);
            redirectAttributes.addFlashAttribute("success", "Thêm menu thành công!");
            return "redirect:/admin/menu";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/menu/add";
        }
    }

    // Edit menu page
    @GetMapping("/edit/{id}")
    public String editMenuPage(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Menu> menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Menu không tồn tại!");
            return "redirect:/admin/menu";
        }

        model.addAttribute("menu", menuOpt.get());
        model.addAttribute("parentMenus", menuService.getRootMenus());
        return "admin/edit-menu";
    }

    // Update menu
    @PostMapping("/edit/{id}")
    public String updateMenu(@PathVariable Integer id,
            @RequestParam String menuName,
            @RequestParam(required = false) Integer parentMenu,
            @RequestParam int position,
            @RequestParam String status,
            @RequestParam(required = false) String url,
            RedirectAttributes redirectAttributes) {
        try {
            Menu menu = new Menu();
            menu.setMenuName(menuName);
            menu.setParentId(parentMenu);
            menu.setPosition(position);
            menu.setStatus(status);
            menu.setUrl(url);

            menuService.updateMenu(id, menu);
            redirectAttributes.addFlashAttribute("success", "Cập nhật menu thành công!");
            return "redirect:/admin/menu";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/menu/edit/" + id;
        }
    }

    // Delete menu (AJAX)
    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteMenu(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            menuService.deleteMenu(id);
            response.put("success", true);
            response.put("message", "Xóa menu thành công!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
