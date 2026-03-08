package kuke.board.like.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import kuke.board.like.entity.ArticleLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleLikeCountRepository extends JpaRepository<ArticleLikeCount, Long> {

    // select ... for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ArticleLikeCount> findLockedByArticleId(Long articleId);

    @Modifying
    @Query(
        value = "update article_like_count set likeCount = likeCount + 1 where articleId = :articleId",
        nativeQuery = true
    )
    int increase(Long articleId);

    @Modifying
    @Query(
        value = "update article_like_count set likeCount = likeCount - 1 where articleId = :articleId",
        nativeQuery = true
    )
    int decrease(Long articleId);
}
