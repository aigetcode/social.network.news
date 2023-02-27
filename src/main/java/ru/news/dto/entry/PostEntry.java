package ru.news.dto.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.news.entity.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEntry {

    private UUID id;
    private Integer version;
    private String title;
    private String userUuid;
    private String description;

    @Builder.Default
    private List<PhotoLinkEntry> photoLinks = new ArrayList<>();

    public static List<PostEntry> fromListPost(List<Post> post) {
        return post.stream()
                .map(PostEntry::fromPost)
                .toList();
    }

    public static PostEntry fromPost(Post post) {
        if (post == null)
            return null;

        PostEntryBuilder postEntry = PostEntry.builder();

        if (post.getId() != null) {
            postEntry.id(post.getId());
        }

        postEntry.version(post.getVersion())
                .title(post.getTitle())
                .description(post.getDescription())
                .userUuid(post.getUserUuid())
                .photoLinks(post.getPhotoLinks().stream()
                        .map(PhotoLinkEntry::fromPhoto)
                        .toList());
        return postEntry.build();
    }
}
