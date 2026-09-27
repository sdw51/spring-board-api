package com.back.member.dto;

import com.back.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private Long id;
    private String email;
    private String nickName;

    public static LoginResponseDto from(Member member) {
        return new LoginResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickName()
        );
    }
}