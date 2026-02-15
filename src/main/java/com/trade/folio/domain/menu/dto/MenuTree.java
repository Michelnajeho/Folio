package com.trade.folio.domain.menu.dto;

import com.trade.folio.domain.menu.entity.Menu;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuTree {

    private Long id;
    private Long parentId;
    private String menuName;
    private String menuCode;
    private String menuUrl;
    private String icon;
    private Integer depth;
    private Integer sortOrder;
    private List<MenuTree> children = new ArrayList<>();

    public static MenuTree from(Menu menu) {
        MenuTree tree = new MenuTree();
        tree.setId(menu.getId());
        tree.setParentId(menu.getParentId());
        tree.setMenuName(menu.getMenuName());
        tree.setMenuCode(menu.getMenuCode());
        tree.setMenuUrl(menu.getMenuUrl());
        tree.setIcon(menu.getIcon());
        tree.setDepth(menu.getDepth());
        tree.setSortOrder(menu.getSortOrder());
        return tree;
    }
}
