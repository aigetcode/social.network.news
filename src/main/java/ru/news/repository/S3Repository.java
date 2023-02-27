package ru.news.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class S3Repository {
    private final AmazonS3 client;
    private final String bucketName;

    public Collection<String> listKeys() {
        return client.listObjectsV2(bucketName, "/").getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    public Collection<String> listKeys(String prefix) {
        return client.listObjectsV2(bucketName, prefix).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    public void delete(String key) {
        client.deleteObject(bucketName, key);
    }

    public void put(String key, InputStream inputStream, ObjectMetadata metadata) {
        client.putObject(bucketName, key, inputStream, metadata);
    }

    public Optional<S3Object> get(String key) {
        try {
            return Optional.ofNullable(client.getObject(bucketName, key));
        } catch (AmazonServiceException exception) {
            return Optional.empty();
        }
    }
}
