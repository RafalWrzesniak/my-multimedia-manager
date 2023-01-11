package wrzesniak.rafal.my.multimedia.manager.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.config.AwsConfiguration;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3Service(AwsConfiguration awsConfiguration) {
        AWSCredentials credentials = new BasicAWSCredentials(
                awsConfiguration.getAccessKey(),
                awsConfiguration.getSecretKey()
        );
        this.bucketName = awsConfiguration.getS3BucketName();
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }

    public void putObject(Path fullFilePath, File fileToPut) {
        s3Client.putObject(bucketName, fullFilePath.toString(), fileToPut);
    }

    public URL getObject(Path objectPath) {
        return s3Client.getUrl(bucketName, objectPath.toString());
    }


}
