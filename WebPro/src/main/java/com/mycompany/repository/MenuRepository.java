package com.mycompany.repository;

import com.mycompany.models.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    // Find all menus ordered by position
    List<Menu> findAllByOrderByPositionAsc();

    // Find menus by parent ID
    List<Menu> findByParentIdOrderByPositionAsc(Integer parentId);

    // Find menus by status
    List<Menu> findByStatusOrderByPositionAsc(String status);

    // Find root menus (parent_id is null)
    List<Menu> findByParentIdIsNullOrderByPositionAsc();

    // Count menus by parent ID
    long countByParentId(Integer parentId);
}
