package com.back.member.controller;

import com.back.member.dto.LoginRequestDto;
import com.back.member.dto.LoginResponseDto;
import com.back.member.dto.SignUpRequestDto;
import com.back.member.dto.SignupResponseDto;
import com.back.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

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
            @Valid @RequestBody LoginRequestDto requestDto,
            HttpServletRequest request
    ) {
        LoginResponseDto responseDto = memberService.Login(requestDto);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                responseDto.getId(),
                null,
                Collections.emptyList()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        return responseDto;
    }
}
