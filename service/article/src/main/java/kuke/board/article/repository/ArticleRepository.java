package kuke.board.article.repository;

import java.util.List;
import kuke.board.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(
        value = """
                SELECT article.article_id, article.title, article.content, article.board_id, article.writer_id, article.created_at, article.modified_at
                FROM (
                    select article_id from article
                    where board_id = :boardId
                    order by article_id desc
                    limit :limit offset :offset
                ) t LEFT JOIN article ON t.article_id = article.article_id
            """,
        nativeQuery = true
    )
    List<Article> findAll(
        @Param("boardId") Long boardId,
        @Param("offset") Long offset,
        @Param("limit") Long limit
    );

    @Query(
        value = """
                SELECT count(*)
                FROM (
                    select article_id from article
                    where board_id = :boardId
                    limit :limit
                ) t
            """,
        nativeQuery = true
    )
    long count(
        @Param("boardId") Long boardId,
        @Param("limit") Long limit
    );
}
