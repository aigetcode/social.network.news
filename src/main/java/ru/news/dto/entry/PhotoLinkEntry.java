package ru.news.dto.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.news.entity.PhotoLink;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoLinkEntry {

    private Long id;
    private Integer version;
    private String postId;
    private String link;

    public static List<PhotoLinkEntry> fromListPhoto(List<PhotoLink> post) {
        return post.stream()
                .map(PhotoLinkEntry::fromPhoto)
                .toList();
    }

    public static PhotoLinkEntry fromPhoto(PhotoLink photoLink) {
        if (photoLink == null)
            return null;

        PhotoLinkEntryBuilder photoLinkEntryBuilder = PhotoLinkEntry.builder();

        if (photoLink.getId() != null) {
            photoLinkEntryBuilder.id(photoLink.getId());
        }

        photoLinkEntryBuilder.version(photoLink.getVersion())
                .postId(photoLink.getPost().getId().toString())
                .link(photoLink.getLink());
        return photoLinkEntryBuilder.build();
    }
}
