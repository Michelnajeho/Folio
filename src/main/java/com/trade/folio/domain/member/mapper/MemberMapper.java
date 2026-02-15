package com.trade.folio.domain.member.mapper;

import com.trade.folio.domain.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    int countByLoginId(@Param("loginId") String loginId);

    int countByEmail(@Param("email") String email);

    Member findByLoginId(@Param("loginId") String loginId);

    void insert(Member member);
}
