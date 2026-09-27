package com.back.post.service;

import com.back.member.entity.Member;
import com.back.member.repository.MemberRepository;
import com.back.post.dto.PostRequestDto;
import com.back.post.dto.PostResponseDto;
import com.back.post.entity.Post;
import com.back.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostResponseDto createPost(Long memberId, PostRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Post post = new Post(
                requestDto.getTitle(),
                requestDto.getContent(),
                member
                );

        Post savePost = postRepository.save(post);

        return PostResponseDto.from(savePost);
    }
}
