package com.back.member.dto;

import com.back.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SignupResponseDto {

    private Long id;
    private String email;
    private String NickName;
    private LocalDateTime createdAt;

    public static SignupResponseDto from(Member member) {
        return new SignupResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickName(),
                member.getCreatedAt()
        );
    }
}
