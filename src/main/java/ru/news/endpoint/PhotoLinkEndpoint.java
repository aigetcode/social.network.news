package ru.news.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import ru.news.dto.SortDto;
import ru.news.dto.entry.PhotoLinkEntry;
import ru.news.endpoint.mvc.Api;
import ru.news.exceptions.ExceptionResponse;
import ru.news.service.PhotoLinkService;

import java.util.List;

@Api
@Validated
@RequestMapping("/v1/posts/{postId}/photos")
@RequiredArgsConstructor
@Tag(name = "Photo link endpoint")
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
public class PhotoLinkEndpoint {
    private final PhotoLinkService photoLinkService;

    @GetMapping
    @Operation(summary = "Получить все фотографии к посту")
    public ResponseEntity<List<PhotoLinkEntry>> getAllPhotos(@PathVariable String postId,
                                                             @RequestParam(value = "sorting", defaultValue = "id,desc",
                                                                    required = false) String[] sortEntries) {
        List<PhotoLinkEntry> users = photoLinkService.getAllPhotosByPostId(postId, SortDto.toSort(sortEntries));
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать фотографии к посту и сохранить в S3")
    public ResponseEntity<List<Long>> create(@PathVariable String postId,
                                             @RequestParam List<MultipartFile> file) {
        List<Long> photoId = photoLinkService.create(postId, file);
        return ResponseEntity.ok(photoId);
    }

    @DeleteMapping("/{photoId}")
    @Operation(summary = "Удалить фотографию по id")
    public ResponseEntity<Void> delete(@PathVariable String postId,
                                       @PathVariable Long photoId) {
        photoLinkService.delete(photoId);
        return ResponseEntity.ok().build();
    }
}
