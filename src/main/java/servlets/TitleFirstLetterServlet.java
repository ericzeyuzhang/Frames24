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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "TitleFirstLetterServlet", urlPatterns = "/api/title-first-letter")
public class TitleFirstLetterServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb-r")
    public DataSource dataSourceRead;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {

            Connection dbcon = dataSourceRead.getConnection();
            Statement statement = dbcon.createStatement();
            String query =  "SELECT DISTINCT(LEFT(m.title, 1)) AS first_letter " +
                            "FROM movies AS m " +
                            "ORDER BY IF(first_letter RLIKE '^[a-z]', 1, 2), first_letter ;";
            ResultSet rs = statement.executeQuery(query);
            JsonArray firstLetters = new JsonArray();
            boolean isOther = false;
            while (rs.next()){
                String firstLetter = rs.getString("first_letter");
                if (firstLetter.matches("[a-zA-Z0-9]*")){
                    firstLetters.add(firstLetter);
                }
                else {
                    isOther = true;
                }
            }
            if (isOther) {
                firstLetters.add("Other");
            }
            out.write(firstLetters.toString());
            response.setStatus(200);
            rs.close();
            dbcon.close();


        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            e.printStackTrace();
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
}
