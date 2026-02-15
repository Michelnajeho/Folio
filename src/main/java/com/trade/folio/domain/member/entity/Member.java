package com.trade.folio.domain.member.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Member {

    private Long id;
    private String loginId;
    private String password;
    private String nickname;
    private String email;
    private String role;
    private String status;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;
}
