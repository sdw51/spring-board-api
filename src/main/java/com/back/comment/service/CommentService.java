package com.back.comment.service;

import com.back.comment.dto.CommentRequestDto;
import com.back.comment.dto.CommentResponseDto;
import com.back.comment.entity.Comment;
import com.back.comment.repository.CommentRepository;
import com.back.member.entity.Member;
import com.back.member.repository.MemberRepository;
import com.back.post.entity.Post;
import com.back.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentResponseDto createComment(
            Long postId,
            Long memberId,
            CommentRequestDto requestDto
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Comment comment = new Comment(
                requestDto.getContent(),
                post,
                member
        );

        Comment saved = commentRepository.save(comment);

        return CommentResponseDto.from(saved);
    }
}
