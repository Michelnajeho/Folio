package com.trade.folio.domain.menu.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Menu {

    private Long id;
    private Long parentId;
    private String menuName;
    private String menuCode;
    private String menuUrl;
    private String icon;
    private Integer depth;
    private Integer sortOrder;
    private Boolean visible;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
