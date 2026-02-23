package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberService {

    private final MemberRepository memberRepository;

    public void createMember() {

    }
}
