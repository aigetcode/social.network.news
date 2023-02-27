package ru.news.endpoint;

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
import org.springframework.web.bind.annotation.RestController;
import ru.news.dto.SortDto;
import ru.news.dto.entry.PostEntry;
import ru.news.dto.input.PostInput;
import ru.news.entity.Post;
import ru.news.service.PostService;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostEndpoint {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostEntry>> getPagePosts(@RequestParam(value = "pageIndex") int pageIndex,
                                                        @RequestParam(value = "pageSize") int pageSize,
                                                        @RequestParam(value = "sorting", defaultValue = "id,desc",
                                                                required = false) String[] sortEntries) {
        Page<PostEntry> users = postService.getPagePosts(pageIndex, pageSize, SortDto.toSort(sortEntries));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostEntry> getPostById(@PathVariable String id) {
        PostEntry user = postService.getPostById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@Valid @RequestBody PostInput userInput) {
        Post post = userInput.toPost();
        UUID uuid = postService.create(post);
        return ResponseEntity.ok(uuid.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.deleteUser(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

}
