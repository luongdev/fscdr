package com.metechvn.logging;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoggingService {
    void logRequest(HttpServletRequest request, Object body);

    void logResponse(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Object body
    );

}
