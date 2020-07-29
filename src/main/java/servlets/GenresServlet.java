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

@WebServlet(name = "GenresServlet", urlPatterns = "/api/genres")
public class GenresServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb-r")
    public DataSource dataSourceRead;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSourceRead.getConnection();
            Statement statement = dbcon.createStatement();
            String query =  "SELECT DISTINCT(g.name) AS genres " +
                            "FROM movies AS m, genres_in_movies AS gim, genres AS g " +
                            "WHERE m.id = gim.movieId AND g.id = gim.genreId " +
                            "ORDER BY genres ";
            ResultSet rs = statement.executeQuery(query);
            JsonArray firstLetters = new JsonArray();
            while (rs.next()){
                firstLetters.add(rs.getString("genres"));
            }
            out.write(firstLetters.toString());
            response.setStatus(200);
            rs.close();
            dbcon.close();


        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
}
