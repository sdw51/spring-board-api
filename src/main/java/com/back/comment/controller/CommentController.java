package com.back.comment.controller;

import com.back.comment.dto.CommentRequestDto;
import com.back.comment.dto.CommentResponseDto;
import com.back.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public CommentResponseDto createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto requestDto,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();

        return commentService.createComment(postId, memberId, requestDto);
    }
}
