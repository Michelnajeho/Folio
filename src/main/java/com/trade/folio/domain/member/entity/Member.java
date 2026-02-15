package com.trade.folio.domain.member.entity;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Member {

    private UUID id;
    private String loginId;
    private String password;
    private String nickname;
    private String email;
    private String role;
    private String status;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;
}
