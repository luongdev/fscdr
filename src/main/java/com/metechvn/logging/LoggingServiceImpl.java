package com.metechvn.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {

    private static final String START_TIME = "start_time";
    private static final String REQUEST_ID = "request_id";
    private static final String PARAMS = "params";
    private static final String REQ_BODY = "req_body";
    private static final String RESP_BODY = "resp_body";
    private static final String ENDPOINT = "endpoint";
    private static final String DURATION = "duration";
    private static final String SLOW_REQUEST = "slow_request";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${opentracing.spring.web.skip-pattern}")
    private String skipPatterns;

    @Value("${custom_log.slow_request_duration}")
    private Integer slowRequestDuration;

    private static final Set<String> SHOULD_LOG_METHODS = new HashSet<>(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
    ));

    @Override
    public void logRequest(
            HttpServletRequest request, Object body
    ) {
        long startTime = System.currentTimeMillis();
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (shouldLogEndPoint(requestURI) && shouldLogMethod(requestMethod)) {
            Map<String, String> reqParams = LoggingUtils.buildParameterMap(request);

            String bodyJson;
            if (body != null) {
                try {
                    bodyJson = MAPPER.writeValueAsString(body);
                    MDC.put(REQ_BODY, bodyJson);
                } catch (Exception e) {
                    log.warn("Failed to parse request body to json " + body, e);
                }
            }

            Map<String, String> headers = LoggingUtils.buildHeaderMap(request);
            String requestId = headers.getOrDefault("x-request-id", UUID.randomUUID().toString());

            try {
                MDC.put(PARAMS, MAPPER.writeValueAsString(reqParams));
            } catch (Exception e) {
                log.warn("Failed to parse request params to json " + body, e);
            }
            MDC.put(ENDPOINT, requestURI);
            MDC.put(START_TIME, String.valueOf(startTime));
            MDC.put(REQUEST_ID, String.valueOf(requestId));

            log.info("Request");
        }
    }

    @Override
    public void logResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            Object body
    ) {
        String requestURI = request.getRequestURI();
//        String endpoint = endpointFromUri(requestURI);

        if (shouldLogEndPoint(requestURI)) {

            String startTimeStr = MDC.get(START_TIME);
            if (startTimeStr != null) {
                long startTime = Long.parseLong(startTimeStr);
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                MDC.put(DURATION, String.valueOf(duration));
                if (duration > slowRequestDuration)
                    MDC.put(SLOW_REQUEST, "true");
            }

            if (body != null) {
                try {
                    String bodyJson = MAPPER.writeValueAsString(body);
                    MDC.put(RESP_BODY, bodyJson);
                } catch (InvalidDefinitionException e) {
                    log.warn("No serializer found for class: " + body.getClass());
                } catch (JsonProcessingException e) {
                    log.warn("Failed to parse response body to json: " + body, e);
                } catch (Exception e) {
                    log.warn("Error while parsing response body to json: " + body, e);
                }
            }
            log.info("Response");
            MDC.clear();
        }
    }

    private String endpointFromUri(String requestURI) {
        String[] split = requestURI.split("\\?");
        return split[0];
    }

    private boolean shouldLogEndPoint(String endpoint) {
        return Arrays.stream(skipPatterns.split("\\|"))
                     .map(Pattern::compile)
                     .noneMatch(pattern -> pattern.matcher(endpoint).find());
    }

    private boolean shouldLogMethod(String method) {
        return SHOULD_LOG_METHODS.contains(method);
    }


}