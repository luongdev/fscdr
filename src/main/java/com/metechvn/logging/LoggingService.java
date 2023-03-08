package com.metechvn.logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoggingService {
    void logRequest(HttpServletRequest request, Object body);

    void logResponse(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Object body
    );

}
