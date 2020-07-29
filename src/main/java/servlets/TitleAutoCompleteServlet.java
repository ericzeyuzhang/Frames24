package servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "TitleAutoCompleteServlet", urlPatterns = "/api/title-auto-complete")
public class TitleAutoCompleteServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb-r")
    public DataSource dataSourceRead;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonArray suggestions = new JsonArray();
        String[] keyWordArr = request.getParameter("title").split("\\s+");
        String query = "SELECT id, title FROM movies " +
                        "WHERE 1=1 ";
        for (String keyWord : keyWordArr){
            query += "AND title REGEXP '(^|\\\\s)" + keyWord + "' ";
        }
        query += "LIMIT 10 ;";
        System.out.println(query);

        try{
            Connection dbcon = dataSourceRead.getConnection();

            Statement stmt = dbcon.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()){
                JsonObject singleSuggestionObj = new JsonObject();
                singleSuggestionObj.addProperty("value", rs.getString("title"));
                singleSuggestionObj.addProperty("data", rs.getString("id"));
                suggestions.add(singleSuggestionObj);
            }
            response.getWriter().write(suggestions.toString());
            rs.close();
            stmt.close();
            dbcon.close();


        }
        catch (Exception e){
            System.out.println("eee" + e.getMessage());

        }

//        System.out.println(String.format("time ecaplse: %.2f ms", (double) (System.nanoTime() - servletStartTime) / 1000000));
    }
}
