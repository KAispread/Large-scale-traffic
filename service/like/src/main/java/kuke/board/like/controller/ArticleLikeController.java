package kuke.board.like.controller;

import kuke.board.like.service.ArticleLikeService;
import kuke.board.like.service.response.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @GetMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    public ArticleLikeResponse read(
        @PathVariable Long articleId,
        @PathVariable Long userId
    ) {
        return articleLikeService.read(articleId, userId);
    }

    @PostMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    public void like(
        @PathVariable Long articleId,
        @PathVariable Long userId
    ) {
        articleLikeService.like(articleId, userId);
    }

    @DeleteMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    public void unlike(
        @PathVariable Long articleId,
        @PathVariable Long userId
    ) {
        articleLikeService.unlike(articleId, userId);
    }
}
