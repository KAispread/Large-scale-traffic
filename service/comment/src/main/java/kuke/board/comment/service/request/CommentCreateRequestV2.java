package kuke.board.comment.service.request;

import lombok.Getter;

@Getter
public class CommentCreateRequestV2 {
    private Long articleId;
    private String comment;
    private String parentPath;
    private Long writerId;
}
