package com.back.post.controller;

import com.back.post.dto.PostRequestDto;
import com.back.post.dto.PostResponseDto;
import com.back.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public PostResponseDto createPost(
            @Valid @RequestBody PostRequestDto requestDto,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return postService.createPost(memberId, requestDto);
    }

}
