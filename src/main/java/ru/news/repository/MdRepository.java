package ru.news.repository;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Component;
import ru.news.properties.S3Properties;

@Component
public class MdRepository extends S3Repository {
    public MdRepository(AmazonS3 client, S3Properties properties) {
        super(client, properties.getBucketMd());
    }
}
