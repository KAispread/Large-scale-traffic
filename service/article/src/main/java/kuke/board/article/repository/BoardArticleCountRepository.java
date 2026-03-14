package kuke.board.article.repository;

import kuke.board.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {

    @Modifying
    @Query(
        value = "update board_article_count set article_count = article_count + 1 where board_id = :boardId",
        nativeQuery = true
    )
    int increase(Long boardId);

    @Modifying
    @Query(
        value = "update board_article_count set article_count = article_count - 1 where board_id = :boardId",
        nativeQuery = true
    )
    int decrease(Long boardId);
}
