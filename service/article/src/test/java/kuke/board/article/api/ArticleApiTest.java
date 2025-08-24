package kuke.board.article.api;

import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse articleResponse = create(new ArticleCreateRequest(
            "hi", "my content", 1L, 1L
        ));
        System.out.println("article = " + articleResponse);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(217985972681523200L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse.class);
    }

    @Test
    void update() {
        update(217985972681523200L);
        ArticleResponse read = read(217985972681523200L);
        System.out.println("response = " + read);
    }

    void update(Long articleId) {
        restClient.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(new ArticleUpdateRequest("hi 2", "my content 22"))
            .retrieve();
    }

    @Test
    void delete() {
        restClient.delete()
            .uri("/v1/articles/{articleId}", 217985972681523200L)
            .retrieve();
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
