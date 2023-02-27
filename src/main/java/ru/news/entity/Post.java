package ru.news.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "POST")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "user_uuid", length = 50, nullable = false)
    private String userUuid;

    @Column(name = "description", length = 5000, nullable = false)
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<PhotoLink> photoLinks = new ArrayList<>();

}
