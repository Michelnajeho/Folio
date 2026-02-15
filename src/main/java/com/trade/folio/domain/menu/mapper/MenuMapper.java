package com.trade.folio.domain.menu.mapper;

import com.trade.folio.domain.menu.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<Menu> findAllVisible();
}
