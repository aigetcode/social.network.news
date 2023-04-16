package ru.news.dao;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Component;
import ru.news.properties.S3Properties;

@Component
public class PhotoPostS3Repository extends S3Repository {
    public PhotoPostS3Repository(AmazonS3 client, S3Properties properties) {
        super(client, properties.getBucketPostPhoto());
    }
}
