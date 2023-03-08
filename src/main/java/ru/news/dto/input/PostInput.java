package ru.news.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import ru.news.entity.Post;

import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostInput {

    private String id;
    private Integer version;
    @NotEmpty
    private String title;
    @NotEmpty
    private String userUuid;
    @NotEmpty
    private String description;

    public Post toPost() {
        var builder = Post.builder();

        if (this.id != null) {
            builder.id(UUID.fromString(this.id));
        }

        return builder
                .version(this.version)
                .title(this.title)
                .description(this.description)
                .userUuid(this.userUuid)
                .build();
    }

}
