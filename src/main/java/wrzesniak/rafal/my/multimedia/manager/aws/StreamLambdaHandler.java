package wrzesniak.rafal.my.multimedia.manager.aws;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.MyMultimediaManagerApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
public class StreamLambdaHandler implements RequestStreamHandler {

    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            log.info("Before spring initialization");
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(MyMultimediaManagerApplication.class);
            log.info("After spring initialization");
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        log.info("Before handling request");
        log.info(new ObjectMapper().writeValueAsString(inputStream));
        handler.proxyStream(inputStream, outputStream, context);
        log.info("After handling request");
        log.info(new ObjectMapper().writeValueAsString(outputStream));

        AwsProxyResponse response = new AwsProxyResponse();
        response.getMultiValueHeaders().put("Access-Control-Allow-Origin", Collections.singletonList("*"));
        response.getMultiValueHeaders().put("Access-Control-Allow-Headers", Arrays.asList("Content-Type", "X-Amz-Date", "Authorization"));
        response.getMultiValueHeaders().put("Access-Control-Allow-Methods", Collections.singletonList("OPTIONS,POST,GET,PUT,DELETE"));

        String jsonResponse = new ObjectMapper().writeValueAsString(response);
        outputStream.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
    }
}

