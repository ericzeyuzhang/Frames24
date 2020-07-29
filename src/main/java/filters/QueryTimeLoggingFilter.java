package filters;

import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "QueryTimeLoggingFilter")
public class QueryTimeLoggingFilter implements Filter {
    private  FilterConfig filterConfig;


    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        System.out.println("time logging filter triggered");
        long servletStartTime = System.nanoTime();



        chain.doFilter(request, response);
        double TS = (double) (System.nanoTime() - servletStartTime) / 1000000;
//        System.out.println(String.format("duration: %.2f ms", (double) (System.nanoTime() - servletStartTime) / 1000000));
        String contextPath = filterConfig.getServletContext().getRealPath("/");

        String xmlFilePath = contextPath + "query_time_log.txt";
        System.out.println(xmlFilePath);
        File logFile = new File(xmlFilePath);
        if (!logFile.exists()){
            logFile.createNewFile();
        }

        FileWriter logWriter = new FileWriter(logFile, true);
        logWriter.write(String.format("TS=%.2f\n", TS));
        logWriter.close();

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {

    }
}
