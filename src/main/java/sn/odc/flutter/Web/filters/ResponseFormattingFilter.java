package sn.odc.flutter.Web.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseFormattingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        if (isSwaggerPath(requestURI)) {
            chain.doFilter(request, response);
        } else {
            ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);

            chain.doFilter(httpRequest, responseWrapper);

            String originalResponseBody = responseWrapper.getCaptureAsString();

            String formattedResponseBody = formatResponse(originalResponseBody);

            httpResponse.setContentType("application/json");
            PrintWriter out = httpResponse.getWriter();
            out.write(formattedResponseBody);
            out.flush();
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }

    private boolean isSwaggerPath(String requestURI) {
        return requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui.html") ||
                requestURI.startsWith("/swagger-resources/");
    }

    private String formatResponse(String originalResponseBody) {
        return String.format("{ \"status\": \"success\", \"data\": %s }", originalResponseBody);
    }


}
