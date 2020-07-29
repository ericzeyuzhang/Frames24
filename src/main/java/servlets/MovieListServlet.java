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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class MovieListServlet
 */
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Resource(name = "jdbc/moviedb-r")
    public DataSource dataSourceRead;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        double TJ = 0.0;
        long startTime;
        response.setContentType("application/json"); // Response mime type

        String start_with = request.getParameter("start_with");
        String genre = request.getParameter("genre");
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");


        int pageNum = Integer.parseInt(request.getParameter("page"));
        int pageCap = Integer.parseInt(request.getParameter("page_cap"));
        String sorting = "movie_" + request.getParameter("sorting");
//        System.out.println(sorting);
        String order = request.getParameter("order");
        if (!sorting.equals("movie_rating") && !sorting.equals("movie_title") && !sorting.equals("movie_year") &&
                !sorting.equals("movie_director") && !sorting.equals("movie_genres")){
            sorting = "movie_title";
            System.out.println("illegal sorting");
        }
        if (!order.equals("asc") && !order.equals("desc")){
            order = "desc";
        }

        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSourceRead.getConnection();
//            Statement statement = dbcon.createStatement();
            String scratchQuery =
                    "SELECT " +
                    "m.id AS movie_id, m.title AS movie_title, " +
                    "m.year AS movie_year, m.director AS movie_director, " +
                    "r.rating AS movie_rating, GROUP_CONCAT(DISTINCT(g.name) SEPARATOR ', ') AS movie_genres " +
                    "FROM " +
                    "movies AS m " +
                    "LEFT JOIN ratings AS r ON m.id = r.movieId " +
                    "LEFT JOIN genres_in_movies AS gim ON m.id = gim.movieId " +
                    "LEFT JOIN genres AS g ON g.id = gim.genreId " +
                    "%s" +
                    "WHERE 1 = 1 %s " +
                    "GROUP BY m.id " +
                    "%s" ;

            String starJoin = "";
            String having = "";

            String userConstrains = "";
            List<String> setStringArr = new ArrayList();
            if (start_with != null && start_with.length() > 0) {
                userConstrains += "AND m.title REGEXP ? ";
                if (!start_with.equalsIgnoreCase("other")){
                    setStringArr.add("^[" + start_with.toUpperCase() + start_with.toLowerCase() + "]+");
                }
                else {
                    setStringArr.add("^[^a-zA-Z0-9]+");
                }
            }

            if (title != null && title.length() > 0) {
                userConstrains += "AND m.title LIKE ? ";
                setStringArr.add("%" + title + "%");
            }
            if (year != null && year.length() > 0) {
                userConstrains += "AND CAST(m.year AS CHAR(4)) LIKE ? ";
                setStringArr.add("%" + year + "%");
            }
            if (director != null && director.length() > 0) {
                userConstrains += "AND m.director LIKE ? ";
                setStringArr.add("%" + director + "%");
            }
            if (star != null && star.length() > 0) {
                starJoin += "LEFT JOIN stars_in_movies AS sim ON m.id = sim.movieId " +
                            "LEFT JOIN stars AS s ON s.id = sim.starId ";
                userConstrains += "AND s.name LIKE ? ";
                setStringArr.add("%" + star + "%");
            }
            if (genre != null && genre.length() > 0) {
                having += "HAVING movie_genres LIKE ? ";
                setStringArr.add("%" + genre + "%");
            }

            String cntQuery = String.format(
                    String.format("SELECT COUNT(1) AS cnt FROM (%s) AS ml; ", scratchQuery),
                    starJoin, userConstrains, having);
            PreparedStatement cntStmt = dbcon.prepareStatement(cntQuery);
            for (int i = 0; i < setStringArr.size(); i++){
                cntStmt.setString(i + 1, setStringArr.get(i));
            }
//            System.out.println(cntStmt.toString());
            startTime = System.nanoTime();
            ResultSet cntResult = cntStmt.executeQuery();
            TJ += (double) (System.nanoTime() - startTime) / 1000000;


            int numResult = 0;
            if (cntResult.next()){
                numResult = cntResult.getInt("cnt");
            }
            cntResult.close();
            cntStmt.close();

            pageNum = Math.min(pageNum, (numResult + pageCap - 1) / pageCap);
            pageNum = Math.max(pageNum, 1);
            int offset = pageCap * (pageNum - 1);

            String pagination = "ORDER BY %s %s LIMIT ? OFFSET ? ; ";
            String baseQuery = String.format(scratchQuery, starJoin, userConstrains, having) +
                    String.format(pagination, sorting, order);

            PreparedStatement baseStmt = dbcon.prepareStatement(baseQuery);
            for (int i = 0; i < setStringArr.size(); i++){
                baseStmt.setString(i + 1, setStringArr.get(i));
            }
            baseStmt.setInt(setStringArr.size() + 1, pageCap);
            baseStmt.setInt(setStringArr.size() + 2, offset);
            startTime = System.nanoTime();
            ResultSet baseResult = baseStmt.executeQuery();
            TJ += (double) (System.nanoTime() - startTime) / 1000000;

            JsonArray movieList = new JsonArray();
            while (baseResult.next()){
                JsonObject singleMovieObj= new JsonObject();
                String movie_id = baseResult.getString("movie_id");
                singleMovieObj.addProperty("movie_id", movie_id);
                singleMovieObj.addProperty("movie_title", baseResult.getString("movie_title"));
                singleMovieObj.addProperty("movie_year", baseResult.getString("movie_year"));
                singleMovieObj.addProperty("movie_director", baseResult.getString("movie_director"));

                singleMovieObj.addProperty("movie_rating", baseResult.getString("movie_rating"));
                singleMovieObj.addProperty("movie_genres", baseResult.getString("movie_genres"));

                String starQuery =
                        "SELECT s.id, s.name " +
                        "FROM movies AS m " +
                        "JOIN stars_in_movies AS sim ON m.id = sim.movieId " +
                        "JOIN stars AS s ON sim.starId = s.id " +
                        "WHERE m.id = ? ; ";
                PreparedStatement starStmt = dbcon.prepareStatement(starQuery);
                starStmt.setString(1, movie_id);
                startTime = System.nanoTime();
                ResultSet starResult = starStmt.executeQuery();
                TJ += (double) (System.nanoTime() - startTime) / 1000000;
                JsonArray starList = new JsonArray();
                while (starResult.next()) {
                    JsonObject singleStarObject = new JsonObject();
                    singleStarObject.addProperty("id", starResult.getString("id"));
                    singleStarObject.addProperty("name", starResult.getString("name"));
                    starList.add(singleStarObject);
                }
                starResult.close();
                starStmt.close();
                singleMovieObj.add("movie_stars", starList);
                movieList.add(singleMovieObj);
            }
            baseResult.close();
            baseStmt.close();
            dbcon.close();

            JsonObject responseObj = new JsonObject();
            responseObj.addProperty("num_result", numResult);
            responseObj.add("movie_list", movieList);

            // write JSON string to output
            out.write(responseObj.toString());

            response.setStatus(200);

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
            TJ = 0.0;
        }
        out.close();
        String contextPath = getServletContext().getRealPath("/");

        String xmlFilePath = contextPath + "query_time_log.txt";
        System.out.println(xmlFilePath);
        File logFile = new File(xmlFilePath);
        if (!logFile.exists()){
            logFile.createNewFile();
        }

        FileWriter logWriter = new FileWriter(logFile, true);
        logWriter.write(String.format("TJ=%.2f, ", TJ));
        logWriter.close();

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}

