package com.trade.folio.domain.member.service;

import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberMapper.findByLoginId(username);
        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 계정입니다: " + username);
        }

        return User.builder()
                .username(member.getLoginId())
                .password(member.getPassword())
                .roles(member.getRole() != null ? member.getRole() : "USER")
                .build();
    }
}
