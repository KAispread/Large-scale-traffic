package kuke.board.comment.repository;

import java.util.List;
import java.util.Optional;
import kuke.board.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentV2Repository extends JpaRepository<CommentV2, Long> {

    @Query("SELECT c FROM CommentV2 c WHERE c.commentPath.path = :path")
    Optional<CommentV2> findByPath(String path);

    @Query(
        value = """
                SELECT path FROM comment_v2 
                WHERE article_id = :articleId 
                    AND path > :pathPrefix 
                    AND path LIKE :pathPrefix 
                ORDER BY path DESC 
                LIMIT 1
                """,
        nativeQuery = true
    )
    Optional<String> findDescendantsTopPath(Long articleId, String pathPrefix);

    @Query(
        value = """
                SELECT comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id, comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at
                FROM (
                                SELECT comment_id FROM comment_v2 WHERE article_id = :articleId ORDER BY path ASC
                                LIMIT :limit OFFSET :offset
                ) t LEFT JOIN comment_v2 ON t.comment_id = comment_v2.comment_id
                """,
        nativeQuery = true
    )
    List<CommentV2> findAll(
        Long articleId,
        Long offset,
        Long limit
    );

    @Query(
        value = """
                SELECT count(*)
                FROM (
                        SELECT comment_id FROM comment_v2 WHERE article_id = :articleId
                        LIMIT :limit
                ) as cnt
                """,
        nativeQuery = true
    )
    Long count(
        Long articleId,
        Long limit
    );

    @Query(
        value = """
                SELECT comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id, comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at
                FROM comment_v2
                WHERE article_id = :articleId
                ORDER BY path
                LIMIT :limit
                """,
        nativeQuery = true
    )
    List<CommentV2> findAllInfiniteScroll(
        Long articleId,
        Long limit
    );

    @Query(
        value = """
                SELECT comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id, comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at
                FROM comment_v2
                WHERE article_id = :articleId AND path > :lastPath
                ORDER BY path
                LIMIT :limit
                """,
        nativeQuery = true
    )
    List<CommentV2> findAllInfiniteScroll(
        Long articleId,
        String lastPath,
        Long limit
    );
}
