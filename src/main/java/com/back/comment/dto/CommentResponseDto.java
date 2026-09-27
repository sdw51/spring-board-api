package com.back.comment.dto;

import com.back.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private String nickName;
    private LocalDateTime createdAt;

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getMember().getNickName(),
                comment.getCreatedAt()
        );
    }
}
