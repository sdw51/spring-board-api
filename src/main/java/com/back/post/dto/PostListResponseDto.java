package com.back.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostListResponseDto {

    private Long id;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
}
