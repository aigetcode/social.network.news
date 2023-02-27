package ru.news.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.news.dto.SortDto;
import ru.news.dto.entry.PhotoLinkEntry;
import ru.news.service.PhotoLinkService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/v1/posts/{postId}/photos")
@RequiredArgsConstructor
public class PhotoLinkEndpoint {
    private final PhotoLinkService photoLinkService;

    @GetMapping
    public ResponseEntity<List<PhotoLinkEntry>> getAllPhotos(@PathVariable String postId,
                                                             @RequestParam(value = "sorting", defaultValue = "id,desc",
                                                                    required = false) String[] sortEntries) {
        List<PhotoLinkEntry> users = photoLinkService.getAllPhotosByPostId(postId, SortDto.toSort(sortEntries));
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@PathVariable String postId,
                                         @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        photoLinkService.create(postId, files);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String postId,
                                           @PathVariable Long photoId) {
        photoLinkService.deletePhotoLink(photoId);
        return ResponseEntity.ok().build();
    }

}
