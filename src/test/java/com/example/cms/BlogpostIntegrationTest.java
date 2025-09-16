package com.example.cms;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.cms.dto.CategoryRequest;
import com.example.cms.dto.CategoryResponse;
import com.example.cms.dto.PostRequest;
import com.example.cms.dto.PostResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BlogpostIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;

    // POST

    private final String title = "Ein Test-Beitrag";
    private final String author = "Frau Müller";
    private final String content = "Das ist der Inhalt des Test-Beitrags";
    private final List<Long> emptyCategoryIds = new ArrayList<>();

    @Test
    void testCreateBlogpostWithoutCategories() {
        PostRequest request = new PostRequest(title, author, content, emptyCategoryIds);
        ResponseEntity<PostResponse> response = restTemplate.postForEntity("/blogposts", request, PostResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().blogpostId()).isNotNull();
        assertThat(response.getBody().versionNumber()).isEqualTo(1);
        assertThat(response.getBody().title()).isEqualTo(title);
        assertThat(response.getBody().author()).isEqualTo(author);
        assertThat(response.getBody().content()).isEqualTo(content);
        assertThat(response.getBody().createdAt()).isNotNull();
        assertThat(response.getBody().createdAt()).isInstanceOf(java.time.LocalDateTime.class);
        assertThat(response.getBody().categories()).isEmpty();
    }

    @Test
    void testCreateBlogpostWithCategories_success() {
        CategoryRequest categoryRequest = new CategoryRequest("Tech", null);
        ResponseEntity<CategoryResponse> categoryResponse = restTemplate.postForEntity("/categories", categoryRequest, CategoryResponse.class);

        PostRequest request = new PostRequest(title, author, content, new ArrayList<>(List.of(categoryResponse.getBody().id())));
        ResponseEntity<PostResponse> response = restTemplate.postForEntity("/blogposts", request, PostResponse.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().categories()).isNotEmpty();
        assertThat(response.getBody().categories().size()).isEqualTo(1);
    } 

    @Test
    void testCreateBlogpostWithCategoriesThatDontExist_failure() {
        PostRequest request = new PostRequest(title, author, content, new ArrayList<>(List.of(1L)));
        ResponseEntity<PostResponse> response = restTemplate.postForEntity("/blogposts", request, PostResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testCreateBlogpostWithEmptyTitle_failure() {
        PostRequest requestNull = new PostRequest(null, author, content, emptyCategoryIds);
        ResponseEntity<PostResponse> responseNull = restTemplate.postForEntity("/blogposts", requestNull, PostResponse.class);
        PostRequest requestEmpty = new PostRequest("", author, content, emptyCategoryIds);
        ResponseEntity<PostResponse> responseEmpty = restTemplate.postForEntity("/blogposts", requestEmpty, PostResponse.class);

        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseNull.getBody()).isNull();
        assertThat(responseEmpty.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEmpty.getBody()).isNull();
    }

    @Test
    void testCreateBlogpostWithEmptyAuthor_failure() {
        PostRequest requestNull = new PostRequest(title, null, content, emptyCategoryIds);
        ResponseEntity<PostResponse> responseNull = restTemplate.postForEntity("/blogposts", requestNull, PostResponse.class);
        PostRequest requestEmpty = new PostRequest(title, "", content, emptyCategoryIds);
        ResponseEntity<PostResponse> responseEmpty = restTemplate.postForEntity("/blogposts", requestEmpty, PostResponse.class);

        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseNull.getBody()).isNull();
        assertThat(responseEmpty.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEmpty.getBody()).isNull();
    }

    @Test
    void testCreateBlogpostWithEmptyContent_failure() {
        PostRequest requestNull = new PostRequest(title, author, null, emptyCategoryIds);
        ResponseEntity<PostResponse> responseNull = restTemplate.postForEntity("/blogposts", requestNull, PostResponse.class);
        PostRequest requestEmpty = new PostRequest(title, author, "", emptyCategoryIds);
        ResponseEntity<PostResponse> responseEmpty = restTemplate.postForEntity("/blogposts", requestEmpty, PostResponse.class);

        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseNull.getBody()).isNull();
        assertThat(responseEmpty.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEmpty.getBody()).isNull();
    }

    // PUT

    @Test
    void testUpdateBlogpostWithValidInput_success() {
        PostRequest createRequest = new PostRequest(title, author, content, emptyCategoryIds);
        ResponseEntity<PostResponse> postBeforeUpdate = restTemplate.postForEntity("/blogposts", createRequest, PostResponse.class);
        Long createdId = postBeforeUpdate.getBody().blogpostId();
        PostRequest update = new PostRequest("Neuer Titel", author, content, emptyCategoryIds);
        HttpEntity<PostRequest> entity = new HttpEntity<>(update);
        ResponseEntity<PostResponse> postAfterUpdate = restTemplate.exchange("/blogposts/{id}", HttpMethod.PUT, entity, PostResponse.class, createdId);

        assertThat(postAfterUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postAfterUpdate.getBody().blogpostId()).isEqualTo(createdId);
        assertThat(postBeforeUpdate.getBody().title()).isNotEqualTo(postAfterUpdate.getBody().title());
        assertThat(postBeforeUpdate.getBody().author()).isEqualTo(postAfterUpdate.getBody().author());
        assertThat(postBeforeUpdate.getBody().content()).isEqualTo(postAfterUpdate.getBody().content());

        assertThat(postBeforeUpdate.getBody().versionNumber()).isEqualTo(1);
        assertThat(postAfterUpdate.getBody().versionNumber()).isEqualTo(2);
    }

    @Test
    void testUpdateBlogpostWithInvalidInput_failure() {
        PostRequest createRequest = new PostRequest(title, author, content, emptyCategoryIds);
        ResponseEntity<PostResponse> postBeforeUpdate = restTemplate.postForEntity("/blogposts", createRequest, PostResponse.class);
        Long createdId = postBeforeUpdate.getBody().blogpostId();
        PostRequest update = new PostRequest("Neuer Titel", null, content, emptyCategoryIds); // one input is null
        HttpEntity<PostRequest> entity = new HttpEntity<>(update);
        ResponseEntity<PostResponse> postAfterUpdate = restTemplate.exchange("/blogposts/{id}", HttpMethod.PUT, entity, PostResponse.class, createdId);

        assertThat(postAfterUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(postBeforeUpdate.getBody().title()).isEqualTo(title);
    }


    // DELETE

    @Test
    void testDeleteBlogpost_success() {
        PostRequest createRequest = new PostRequest(title, author, content, emptyCategoryIds);
        ResponseEntity<PostResponse> postResponse = restTemplate.postForEntity("/blogposts", createRequest, PostResponse.class);
        Long createdId = postResponse.getBody().blogpostId();
        ResponseEntity<Void> response = restTemplate.exchange("/blogposts/{id}", HttpMethod.DELETE, null, Void.class, createdId);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<PostResponse> getResponse = restTemplate.getForEntity("/blogposts/{id}", PostResponse.class, createdId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteBlogpostThatDoesntExist_failure() {
        ResponseEntity<Void> response = restTemplate.exchange("/blogposts/{id}", HttpMethod.DELETE, null, Void.class, 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    
}
