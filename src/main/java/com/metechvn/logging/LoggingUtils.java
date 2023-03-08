package com.metechvn.logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoggingUtils {
    public static void onRequestContainsAttr(
            HttpServletRequest request,
            String key,
            BiConsumer<HttpServletRequest, String> callBack
    ) {
        if (request.getAttribute(key) != null) callBack.accept(request, key);
    }

    public static Map<String, String> buildParameterMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String val = request.getParameter(key);
            map.put(key, val);
        }

        return map.isEmpty() ? null : map;
    }

    public static Map<String, String> buildHeaderMap(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String val = request.getHeader(key);
            map.put(key, val);
        }
        return map;
    }

    public static Map<String, String> buildHeaderMap(HttpServletResponse response) {
        return response.getHeaderNames().stream().collect(Collectors.toMap(
                header -> header,
                response::getHeader
        ));
    }

    private Map<String, String> pathParamsFromUri(String requestURI) {
        Map<String, String> results = null;
        String[] split = requestURI.split("\\?");
        if (split.length > 1) {
            String reqPath = IntStream.range(1, split.length)
                                      .boxed()
                                      .map(i -> split[i])
                                      .collect(Collectors.joining("?"));
            String[] pathSplit = reqPath.split("&");
            results = Arrays.stream(pathSplit)
                            .map(ps -> ps.split("="))
                            .filter(sp -> sp.length == 2)
                            .collect(Collectors.toMap(sp -> sp[0], sp -> sp[1]));
        }
        return results;
    }
}
