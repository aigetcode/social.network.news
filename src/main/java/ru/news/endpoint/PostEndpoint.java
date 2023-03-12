package ru.news.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.news.dto.SortDto;
import ru.news.dto.entry.PostEntry;
import ru.news.dto.input.PostInput;
import ru.news.endpoint.mvc.Api;
import ru.news.entity.Post;
import ru.news.exceptions.ExceptionResponse;
import ru.news.service.PostService;

import java.util.UUID;

@Api
@Validated
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post endpoint")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.desc}"),
        @ApiResponse(responseCode = "400", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class))},
                description = "${api.response-codes.badRequest.desc}"),
        @ApiResponse(responseCode = "404", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class))},
                description = "${api.response-codes.notFound.desc}")
})
public class PostEndpoint {
    private final PostService postService;

    @GetMapping
    @Operation(summary = "Get page posts")
    public ResponseEntity<Page<PostEntry>> getPagePosts(@RequestParam(value = "pageIndex") int pageIndex,
                                                        @RequestParam(value = "pageSize") int pageSize,
                                                        @RequestParam(value = "sorting", defaultValue = "title,desc",
                                                                required = false) String[] sortEntries) {
        Page<PostEntry> users = postService.getPagePosts(pageIndex, pageSize, SortDto.toSort(sortEntries));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by id")
    public ResponseEntity<PostEntry> getPostById(@PathVariable String id) {
        PostEntry user = postService.getPostById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create post")
    public ResponseEntity<String> create(@Valid @RequestBody PostInput userInput) {
        Post post = userInput.toPost();
        UUID uuid = postService.create(post);
        return ResponseEntity.ok(uuid.toString());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.deletePost(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}