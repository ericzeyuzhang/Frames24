package filters;

import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURL = httpRequest.getRequestURI();
        if (httpRequest.getSession().getAttribute("user") == null) {
            if (requestURL.contains("api")) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("status", "redirect");
                httpResponse.getWriter().write(responseJson.toString());
                System.out.println("filter api");
            }
            else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
            }
            return;
        }
        else {
            chain.doFilter(request, response);
            System.out.println("filter screening passed");
        }

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());
        System.out.println("ref: " + httpRequest.getHeader("referer"));
        System.out.println("ref: " + httpRequest.getContextPath());
//        // Check if this URL is allowed to access without logging in
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//            // Keep default action: pass along the filter chain
//            chain.doFilter(request, response);
//            return;
//        }

    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
//        allowedURIs.add("login.html");
//        allowedURIs.add("index.html");
//        allowedURIs.add("search.html");
//        allowedURIs.add(".js");
//        allowedURIs.add("api/login");

    }

    public void destroy() {
        // ignored.
    }

}
