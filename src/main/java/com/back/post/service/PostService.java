package com.back.post.service;

import com.back.global.exception.NotFoundException;
import com.back.member.entity.Member;
import com.back.member.repository.MemberRepository;
import com.back.post.dto.PostListResponseDto;
import com.back.post.dto.PostRequestDto;
import com.back.post.dto.PostResponseDto;
import com.back.post.dto.PostUpdateDto;
import com.back.post.entity.Post;
import com.back.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostResponseDto createPost(Long memberId, PostRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Post post = new Post(
                requestDto.getTitle(),
                requestDto.getContent(),
                member
                );

        Post savePost = postRepository.save(post);

        return PostResponseDto.from(savePost);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateDto updateDto, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        post.update(
                updateDto.getTitle(),
                updateDto.getContent()
        );

        return PostResponseDto.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        postRepository.delete(post);

    }

    @Transactional(readOnly = true)
    public Page<PostListResponseDto> getPosts(Pageable pageable) {
        return postRepository.findPostList(pageable);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        return PostResponseDto.from(post);
    }
}
