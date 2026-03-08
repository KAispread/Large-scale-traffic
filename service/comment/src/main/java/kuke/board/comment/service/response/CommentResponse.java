package kuke.board.comment.service.response;

import java.time.LocalDateTime;
import kuke.board.comment.entity.Comment;
import kuke.board.comment.entity.CommentV2;
import lombok.Getter;

@Getter
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long articleId;
    private Long writerId;
    private String path;
    private Boolean deleted;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.commentId = comment.getCommentId();
        commentResponse.content = comment.getContent();
        commentResponse.parentCommentId = comment.getParentCommentId();
        commentResponse.articleId = comment.getArticleId();
        commentResponse.writerId = comment.getWriterId();
        commentResponse.deleted = comment.getDeleted();
        commentResponse.createdAt = comment.getCreatedAt();
        return commentResponse;
    }

    public static CommentResponse from(CommentV2 comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.commentId = comment.getCommentId();
        commentResponse.content = comment.getContent();
        commentResponse.path = comment.getCommentPath().getPath();
        commentResponse.articleId = comment.getArticleId();
        commentResponse.writerId = comment.getWriterId();
        commentResponse.deleted = comment.getDeleted();
        commentResponse.createdAt = comment.getCreatedAt();
        return commentResponse;
    }
}
