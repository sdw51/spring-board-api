package com.back.member.controller;

import com.back.member.dto.LoginRequestDto;
import com.back.member.dto.LoginResponseDto;
import com.back.member.dto.SignUpRequestDto;
import com.back.member.dto.SignupResponseDto;
import com.back.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public SignupResponseDto signUp(
            @Valid @RequestBody SignUpRequestDto requestDto
    ) {
        return memberService.signUp(requestDto);
    }

    @PostMapping("/login")
    public LoginResponseDto Login(
            @Valid @RequestBody LoginRequestDto requestDto
    ) {
        return memberService.Login(requestDto);
    }
}
