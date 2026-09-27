package com.back.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(min = 4, message = "비밀번호는 4글자 이상이어야 합니다.")
    private String password;

    private String nickName;
}
