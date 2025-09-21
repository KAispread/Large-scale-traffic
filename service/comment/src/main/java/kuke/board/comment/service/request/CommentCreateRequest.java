package kuke.board.comment.service.request;

import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private Long articleId;
    private String comment;
    private Long parentCommentId;
    private Long writerId;
}
