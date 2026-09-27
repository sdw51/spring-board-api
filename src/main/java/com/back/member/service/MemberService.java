package com.back.member.service;

import com.back.member.dto.LoginRequestDto;
import com.back.member.dto.LoginResponseDto;
import com.back.member.dto.SignUpRequestDto;
import com.back.member.dto.SignupResponseDto;
import com.back.member.entity.Member;
import com.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signUp(SignUpRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberRepository.existsByNickName(requestDto.getNickName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encoderPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = new Member(
                requestDto.getEmail(),
                encoderPassword,
                requestDto.getNickName()
        );

        Member savedMember = memberRepository.save(member);

        return SignupResponseDto.from(savedMember);
    }

    public LoginResponseDto Login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "이메일이 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException(
                    "이메일 또는 비밀번호가 일치하지 않습니다."
            );
        }

        return LoginResponseDto.from(member);
    }
}
