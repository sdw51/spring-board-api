package com.back.comment.controller;

import com.back.comment.dto.CommentRequestDto;
import com.back.comment.dto.CommentResponseDto;
import com.back.comment.dto.CommentUpdateRequestDto;
import com.back.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}")
    public CommentResponseDto createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto requestDto,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();

        return commentService.createComment(postId, memberId, requestDto);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto requestDto,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();

        return commentService.updateComment(commentId, memberId, requestDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();

        commentService.deleteComment(commentId, memberId);
    }
}
