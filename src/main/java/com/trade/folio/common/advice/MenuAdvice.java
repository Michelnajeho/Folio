package com.trade.folio.common.advice;

import com.trade.folio.domain.menu.dto.MenuTree;
import com.trade.folio.domain.menu.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class MenuAdvice {

    private final MenuService menuService;

    private static final Set<String> EXCLUDED_PATHS = Set.of("/", "/login", "/join");

    @ModelAttribute("menus")
    public List<MenuTree> menus(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.contains(path)) {
            return Collections.emptyList();
        }
        return menuService.getMenuTree();
    }
}
