package com.trade.folio.domain.member.service;

import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.dto.JoinRequest;
import com.trade.folio.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public boolean isLoginIdAvailable(String loginId) {
        return memberMapper.countByLoginId(loginId) == 0;
    }

    public boolean isEmailAvailable(String email) {
        return memberMapper.countByEmail(email) == 0;
    }

    public void join(JoinRequest request) {
        if (!isLoginIdAvailable(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }
        if (!isEmailAvailable(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Member member = new Member();
        member.setLoginId(request.getLoginId());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setNickname(request.getNickname());
        member.setEmail(request.getEmail());

        memberMapper.insert(member);
    }
}
