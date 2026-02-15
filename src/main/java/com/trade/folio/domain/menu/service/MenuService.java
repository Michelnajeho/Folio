package com.trade.folio.domain.menu.service;

import com.trade.folio.domain.menu.dto.MenuTree;
import com.trade.folio.domain.menu.entity.Menu;
import com.trade.folio.domain.menu.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;

    public List<MenuTree> getMenuTree() {
        List<Menu> menus = menuMapper.findAllVisible();

        Map<Long, MenuTree> map = new LinkedHashMap<>();
        for (Menu menu : menus) {
            map.put(menu.getId(), MenuTree.from(menu));
        }

        List<MenuTree> roots = new ArrayList<>();
        for (MenuTree node : map.values()) {
            if (node.getParentId() == null) {
                roots.add(node);
            } else {
                MenuTree parent = map.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return roots;
    }
}
