package kuke.board.comment.repository;

import java.util.List;
import kuke.board.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
        value = """
                select count(*) from(
                    select comment_id from comment
                    where article_id = :articleId
                      and parent_comment_id = :parentCommentId
                    LIMIT :limit
                ) as cci
            """,
        nativeQuery = true
    )
    Long countBy(
        @Param("articleId") Long articleId,
        @Param("parentCommentId") Long parentCommentId,
        @Param("limit") Long limit
    );

    @Query(
        value = """
                SELECT comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, comment.writer_id, comment.deleted, comment.created_at
                FROM (
                                SELECT comment_id FROM comment 
                                WHERE article_id = :articleId
                                ORDER BY parent_comment_id asc, comment_id asc
                                LIMIT :limit OFFSET :offset 
                     ) t LEFT JOIN comment ON t.comment_id = comment.comment_id
                """,
        nativeQuery = true
    )
    List<Comment> findAll(
        Long articleId,
        Long offset,
        Long limit
    );

    @Query(
        value = """
                SELECT COUNT(*) FROM (
                                SELECT comment_id FROM comment WHERE article_id = :articleId LIMIT :limit
                ) t
                """,
        nativeQuery = true
    )
    Long countBy(
        Long articleId,
        Long limit
    );

    @Query(
        value = """
                SELECT comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, comment.writer_id, comment.deleted, comment.created_at
                FROM comment
                WHERE article_id = :articleId
                ORDER BY parent_comment_id, comment_id
                LIMIT :limit
                """,
        nativeQuery = true
    )
    List<Comment> findAllInfiniteScroll(
        Long articleId,
        Long limit
    );

    @Query(
        value = """
                SELECT comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, comment.writer_id, comment.deleted, comment.created_at
                FROM comment
                WHERE article_id = :articleId
                    AND (parent_comment_id > :lastParentCommentId OR (parent_comment_id = :lastParentCommentId AND comment_id > :lastCommentId))
                ORDER BY parent_comment_id, comment_id
                LIMIT :limit
                """,
        nativeQuery = true
    )
    List<Comment> findAllInfiniteScroll(
        Long articleId,
        Long lastParentCommentId,
        Long lastCommentId,
        Long limit
    );
}
