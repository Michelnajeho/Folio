package com.trade.folio.common.advice;

import com.trade.folio.domain.menu.dto.MenuTree;
import com.trade.folio.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class MenuAdvice {

    private final MenuService menuService;

    @ModelAttribute("menus")
    public List<MenuTree> menus() {
        return menuService.getMenuTree();
    }
}
