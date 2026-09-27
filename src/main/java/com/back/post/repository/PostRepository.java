package com.back.post.repository;

import com.back.post.dto.PostListResponseDto;
import com.back.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
    SELECT new com.back.post.dto.PostListResponseDto(
        p.id,
        p.title,
        p.member.nickName,
        p.createdAt,
        COUNT(c.id)
    )
    FROM Post p
    LEFT JOIN Comment c ON c.post = p
    GROUP BY p.id, p.title, p.member.nickName, p.createdAt
    ORDER BY p.createdAt DESC
""")
    Page<PostListResponseDto> findPostList(Pageable pageable);
}
