package ru.news.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.news.repository.HtmlRepository;
import ru.news.repository.MdRepository;
import ru.news.repository.PhotoPostRepository;
import ru.news.repository.S3Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

@Service
public record StorageService(MdRepository mdRepository,
                             HtmlRepository htmlRepository,
                             PhotoPostRepository photoPostRepository) {
    public Collection<String> getAllMdFiles() {
        return mdRepository.listKeys("/");
    }

    public InputStream getMdFileContent(String key) {
        return mdRepository.get(key)
                .map(S3Object::getObjectContent)
                .orElseThrow(() -> new IllegalStateException("Object not found " + key));
    }

    public void putHtmlFile(String key, File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setContentType(MediaType.TEXT_HTML_VALUE);
            htmlRepository.put(key, fileInputStream, metadata);
        }
    }

    public void putImageFile(String key, File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setContentType(Files.probeContentType(Paths.get(file.toURI())));
            PutObjectResult putObjectResult = photoPostRepository.put(key, fileInputStream, metadata);
            System.out.println(putObjectResult);
        }
    }

    public LocalDateTime getMdFileModificationTime(String key) {
        return getObjectLastModified(mdRepository, key);
    }

    public LocalDateTime getHtmlFileModificationTime(String key) {
        return getObjectLastModified(htmlRepository, key);
    }

    private LocalDateTime getObjectLastModified(S3Repository repository, String key) {
        return repository.get(key)
                .map(S3Object::getObjectMetadata)
                .map(ObjectMetadata::getLastModified)
                .map(this::dateToLocal)
                .orElse(LocalDateTime.MIN);
    }

    private LocalDateTime dateToLocal(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
