package swaggy.hidden.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by rcruz on 07-06-2016.
 */
@Component
public class ApiKeyFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        String key = req.getParameter("api_key");

        WrappedRequest wrappedRequest = new WrappedRequest((HttpServletRequest) req);
        wrappedRequest.addHeader("Authorization", "Bearer " + key);
        chain.doFilter(wrappedRequest, res);
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

}
