package ru.news.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.news.properties.S3Properties;

@Configuration
@RequiredArgsConstructor
public class S3Configuration {
    private final S3Properties s3Properties;

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(AWSCredentials credentials) {
        return new AWSStaticCredentialsProvider(credentials);
    }

    @Bean
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride(s3Properties.getSigner());
        return clientConfiguration;
    }

    @Bean
    public AmazonS3 amazonS3(AWSCredentialsProvider provider, ClientConfiguration configuration) {
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                s3Properties.getEndpoint(),
                                s3Properties.getRegion()))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(configuration)
                .withCredentials(provider)
                .build();
    }
}
