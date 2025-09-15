package com.example.cms;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.example.cms.dto.CategoryRequest;
import com.example.cms.dto.CategoryResponse;
import com.example.cms.dto.PostRequest;
import com.example.cms.dto.PostResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;

    // POST

    @Test
    void testCreateCategoryWithDescription_success() {
        CategoryRequest request = new CategoryRequest("Tech", "Tech news");

        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity("/categories", request, CategoryResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Tech");
        assertThat(response.getBody().description()).isEqualTo("Tech news");
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void testCreateCategoryWithoutDescription_success() {
        CategoryRequest request = new CategoryRequest("Tech", null);

        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity("/categories", request, CategoryResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Tech");
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().description()).isNull();
    }

    @Test
    void testCreateCategory_twoCategories_differentIds() {
        CategoryRequest requestOne = new CategoryRequest("Tech", null);
        CategoryRequest requestTwo = new CategoryRequest("Sport", "Sport news");
        ResponseEntity<CategoryResponse> responseOne = restTemplate.postForEntity("/categories", requestOne, CategoryResponse.class);
        ResponseEntity<CategoryResponse> responseTwo = restTemplate.postForEntity("/categories", requestTwo, CategoryResponse.class);

        assertThat(responseOne.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseTwo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseOne.getBody().id()).isNotEqualTo(responseTwo.getBody().id());
    }

    @Test
    void testCreateCategoryWithoutName_badRequest() {
        CategoryRequest request = new CategoryRequest(null, null);
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity("/categories", request, CategoryResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testCreateCategoryWithEmptyName_badRequest() {
        CategoryRequest request = new CategoryRequest("", null);
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity("/categories", request, CategoryResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    // PUT

    @Test
    void testUpdateCategoryNameOfExistingCategory() {
        CategoryRequest firstRequest = new CategoryRequest("Tech", "Tech news");
        ResponseEntity<CategoryResponse> firstResponse = restTemplate.postForEntity("/categories", firstRequest, CategoryResponse.class);
        Long createdId = firstResponse.getBody().id();
        CategoryRequest secondRequest = new CategoryRequest("Technology", null);
        HttpEntity<CategoryRequest> entity = new HttpEntity<>(secondRequest);
        ResponseEntity<CategoryResponse> secondResponse = restTemplate.exchange("/categories/{id}", HttpMethod.PUT, entity, CategoryResponse.class, createdId);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponse.getBody().id()).isEqualTo(createdId);
        assertThat(firstResponse.getBody().name()).isNotEqualTo(secondResponse.getBody().name());
        assertThat(firstResponse.getBody().id()).isEqualTo(secondResponse.getBody().id());
    }

    @Test 
    void testUpdateCategoryNameOfNonexistingCategory() {
        CategoryRequest updateRequest = new CategoryRequest("Technology", null);
        HttpEntity<CategoryRequest> entity = new HttpEntity<>(updateRequest);
        ResponseEntity<CategoryResponse> updateResponse = restTemplate.exchange("/categories/{id}", HttpMethod.PUT, entity, CategoryResponse.class, 1L);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateCategoryWithInvalidInput() {
        CategoryRequest firstRequest = new CategoryRequest("Tech", "Tech news");
        ResponseEntity<CategoryResponse> firstResponse = restTemplate.postForEntity("/categories", firstRequest, CategoryResponse.class);
        Long createdId = firstResponse.getBody().id();
        CategoryRequest secondRequest = new CategoryRequest("", null);
        HttpEntity<CategoryRequest> entity = new HttpEntity<>(secondRequest);
        ResponseEntity<CategoryResponse> secondResponse = restTemplate.exchange("/categories/{id}", HttpMethod.PUT, entity, CategoryResponse.class, createdId);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(firstResponse.getBody().name()).isEqualTo("Tech");
    }

    // DELETE

    @Test
    void testDeleteCategoryWithoutBlogpost_success() {
        CategoryRequest request = new CategoryRequest("Tech", "Tech news");
        ResponseEntity<CategoryResponse> postResponse = restTemplate.postForEntity("/categories", request, CategoryResponse.class);
        Long createdId = postResponse.getBody().id();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/categories/{id}", HttpMethod.DELETE, null, Void.class, createdId);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<CategoryResponse> getResponse = restTemplate.getForEntity("/categories/{id}", CategoryResponse.class, createdId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNull();
    }

    @Test
    void testDeleteCategoryWithoutBlogpost_failure() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/categories/{id}", HttpMethod.DELETE, null, Void.class, 1L);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteCategory_alsoDeleteInBlogpost() {
        CategoryRequest request = new CategoryRequest("Tech", "Tech news");
        ResponseEntity<CategoryResponse> categoryResponse = restTemplate.postForEntity("/categories", request, CategoryResponse.class);
        Long createdId = categoryResponse.getBody().id();
        PostRequest blogpostRequest = new PostRequest("Ein Test", "Frau Müller", "Das ist der Inhalt des Testbeitrags", new ArrayList<>(List.of(createdId)));
        ResponseEntity<PostResponse> blogpostResponse = restTemplate.postForEntity("/blogposts", blogpostRequest, PostResponse.class);

        assertThat(blogpostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(blogpostResponse.getBody().categories()).isNotEmpty();
        assertThat(blogpostResponse.getBody().categories()).contains(categoryResponse.getBody().name());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/categories/{id}", HttpMethod.DELETE, null, Void.class, createdId);
        ResponseEntity<PostResponse> getBlogpostResponse = restTemplate.getForEntity("/blogposts/{id}", PostResponse.class, createdId);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(getBlogpostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getBlogpostResponse.getBody().categories()).isEmpty();
    }

    @Test
    void testSafelyDeleteCategoryWithReferenceInBlogpost() {
        CategoryRequest request = new CategoryRequest("Tech", "Tech news");
        ResponseEntity<CategoryResponse> categoryResponse = restTemplate.postForEntity("/categories", request, CategoryResponse.class);
        Long createdId = categoryResponse.getBody().id();
        PostRequest blogpostRequest = new PostRequest("Ein Test", "Frau Müller", "Das ist der Inhalt des Testbeitrags", new ArrayList<>(List.of(createdId)));
        ResponseEntity<PostResponse> blogpostResponse = restTemplate.postForEntity("/blogposts", blogpostRequest, PostResponse.class);

        assertThat(blogpostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(blogpostResponse.getBody().categories()).isNotEmpty();
        assertThat(blogpostResponse.getBody().categories()).contains(categoryResponse.getBody().name());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/categories/{id}/safe", HttpMethod.DELETE, null, Void.class, createdId);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<PostResponse> getBlogpostResponse = restTemplate.getForEntity("/blogposts/{id}", PostResponse.class, createdId);
        assertThat(getBlogpostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getBlogpostResponse.getBody().categories()).isNotEmpty();
    }

    @Test
    void testSafelyDeleteCategoryWithoutReferenceInBlogpost() {
        CategoryRequest request = new CategoryRequest("Tech", "Tech news");
        ResponseEntity<CategoryResponse> categoryResponse = restTemplate.postForEntity("/categories", request, CategoryResponse.class);
        Long createdId = categoryResponse.getBody().id();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/categories/{id}/safe", HttpMethod.DELETE, null, Void.class, createdId);
        ResponseEntity<CategoryResponse> getResponse = restTemplate.getForEntity("/categories/{id}", CategoryResponse.class, createdId);

        assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
