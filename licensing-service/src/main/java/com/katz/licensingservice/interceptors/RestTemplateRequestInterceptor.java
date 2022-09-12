package com.katz.licensingservice.interceptors;

import com.katz.licensingservice.utils.UserContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.katz.licensingservice.utils.Definitions.CORRELATION_ID;

public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();

        // Makes sure that correlation id - is propagated to the down stream services for tracking
        headers.add(
                CORRELATION_ID,
                UserContextHolder
                        .get()
                        .getCorrelationId()
        );
        return execution.execute(request, body);
    }
}
