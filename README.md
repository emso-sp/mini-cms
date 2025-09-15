# Mini CMS

A small Java REST service for a content management system (CMS) for blog posts.  
Data is stored **in-memory** using Java collections (Maps).  

This CMS supports editing blog post statuses, viewing different versions of a blog post, and rolling back to previous versions.

---

## Motivation

This project was created to demonstrate my understanding of:

- REST API design with Spring Boot  
- Separation of concerns using Controller → Service → Repository  
- DTOs and entity mapping  
- Blog post workflows including versioning and status management  

It serves as a portfolio piece and a practical example for technical interviews.

---

## Architecture Overview

The application follows a **three-layer architecture**: Controller → Service → Repository → Entities/DTOs

- **Controller**: handles HTTP requests and responses (uses DTOs)  
- **Service**: contains business logic (status workflow, versioning, validation)  
- **Repository**: manages in-memory data persistence  
- **DTOs & Mappers**: separate internal entities from exposed API contracts  

This structure ensures clean separation of concerns and easier testing/maintenance.

---

## Key Implementation Highlights

- **Versioning**: Each blog post maintains multiple versions. You can roll back to previous versions.  
- **Status Workflow**: Blog posts have statuses (`DRAFT`, `PUBLISHED`, `ARCHIVED`). Editing resets status to `DRAFT`. 
- **Filtering of Blog posts by Category**: Blog posts can have no or multiple categories. You can filter blog posts by categories.
- **Safe Deletion**: Categories cannot be deleted if referenced by any blog post.  
- **DTO/Entity Separation**: Controllers interact with DTOs while repositories handle entities.  
- **In-Memory Persistence**: Uses Java `Map` collections for simplicity (no database required).  

---

## Features / Endpoints

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/categories` | List all categories |
| GET    | `/categories/{id}` | Get a category by ID |
| POST   | `/categories` | Create a new category |
| PUT    | `/categories/{id}` | Update an existing category |
| PATCH  | `/categories/{id}` | Partially update a category |
| DELETE | `/categories/{id}` | Hard delete a category |
| DELETE | `/categories/{id}/safe` | Safe delete: only deletes if not referenced by any blog post |

### Blogposts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/blogposts` | List all blog posts (optionally filtered by category) |
| GET    | `/blogposts/{id}` | Get a blog post by ID |
| GET    | `/blogposts/{id}/versions` | Get all versions of a blog post |
| POST   | `/blogposts` | Create a new blog post |
| PUT    | `/blogposts/{id}` | Update an existing blog post (resets status to DRAFT) |
| PATCH  | `/blogposts/{id}` | Partially update a blog post (resets status to DRAFT) |
| PUT    | `/blogposts/{id}/status` | Change the status of a blog post (DRAFT, PUBLISHED, ARCHIVED) |
| PUT    | `/blogposts/{id}/rollback` | Rollback a blog post to a previous version |
| DELETE | `/blogposts/{id}` | Delete a blog post (removes all versions) |

**Functionality included:**
- CRUD for blog posts  
- CRUD for categories  
- Status workflow for blog posts  
- Versioning of blog posts  
- Filtering blog posts by category  
- Safe and hard deletion of categories  

---

## Technical Setup

- **Java**: 21
- **Spring Boot**: 3.5.5  
- **Persistence**: In-memory using Java `Map` collections  
- **Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

---

## Running the Project

1. Clone the project: 
```bash 
git clone https://github.com/yourusername/mini-cms.git 
```
2. Import the project into your IDE (IntelliJ, Eclipse, etc.)
3. Run the application: 
```bash
mvn spring-boot:run
# or
./gradlew bootRun
```
4. Access Swagger UI for API documentation: http://localhost:8080/swagger-ui/index.html. Alternatively, you can copy the OpenAPI YAML located in `docs/` into https://editor.swagger.io/

---

## Testing

Integration tests are provided for blog posts and categories, covering POST, PUT, and DELETE operations.

Run tests with:
```bash
mvn test
# or
./gradlew test
```

---

## Usage Examples
### Create Category
```bash
POST /categories
{
  "name": "Tech",
  "description": "Tech news"
}
```
### Create Blog Post
```bash
POST /blogposts
{
  "title": "New Java Release",
  "author": "Alice",
  "content": "Many new features ...",
  "categoryIds": [1, 2]
}
```
### Filter by Categories
```bash
GET /blogposts?categoryId=1&categoryId=2
```
### Update Blog Post Status
```bash
PUT /blogposts/1/status
{
  "status": "PUBLISHED"
}
```
### Rollback to a Previous Version
```bash
PUT /blogposts/1/rollback?version=2
```
