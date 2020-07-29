package servlets;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb-w")
    public DataSource dataSourceWrite;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObj = new JsonObject();

        try {
            Connection dbcon = dataSourceWrite.getConnection();
            CallableStatement statement;
            String type = request.getParameter("type");
            String msg;
            switch (type){
                case "star": {
                    String star_name = request.getParameter("name");
                    String star_birthYear = request.getParameter("birth-year");
                    if (star_name == null || star_name.length() == 0) {
                        msg = "Invalid input";
                        break;
                    }
                    statement = dbcon.prepareCall("{CALL insert_star(?, ?, ?, ?)}");
                    statement.setString(1, star_name);
                    if (star_birthYear == null || star_birthYear.length() == 0)
                        statement.setNull(2, Types.NULL);
                    else
                        statement.setInt(2, Integer.parseInt(star_birthYear));

                    statement.registerOutParameter(3, Types.BOOLEAN);
                    statement.registerOutParameter(4, Types.VARCHAR);
                    statement.execute();
                    Boolean star_isExist = statement.getBoolean(3);
                    String star_id = statement.getString(4);

                    if (star_isExist)
                        msg = String.format("Star '%s' (id=%s) already exists. ", star_name, star_id);
                    else
                        msg = String.format("Star '%s' added, with id = %s. ", star_name, star_id);

                    statement.close();
                    break;
                }
                case "genre": {
                    String genre_name = request.getParameter("name");
                    if (genre_name == null || genre_name.length() == 0) {
                        msg = "Invalid input";
                        break;
                    }
                    statement = dbcon.prepareCall("{CALL insert_genre(?, ?, ?)}");
                    statement.setString(1, genre_name);
                    statement.registerOutParameter(2, Types.BOOLEAN);
                    statement.registerOutParameter(3, Types.INTEGER);
                    statement.execute();
                    Boolean genre_isExist = statement.getBoolean(2);
                    int genre_id = statement.getInt(3);
                    if (genre_isExist)
                        msg = String.format("Genre '%s' (id=%d) already exists. ", genre_name, genre_id);
                    else
                        msg = String.format("Genre '%s' added, with id = %d. ", genre_name, genre_id);
                    statement.close();
                    break;
                }
                case "movie": {
                    String movie_title = request.getParameter("title");
//                    String movie_year = request.getParameter("year");
                    int movie_year;
                    try {
                        movie_year = Integer.parseInt(request.getParameter("year"));
                    } catch (Exception e) {
                        msg = "Invalid input";
                        break;
                    }

                    String movie_director = request.getParameter("director");

                    if (movie_title == null || movie_title.length() == 0 ||
                            movie_director == null || movie_director.length() == 0) {
                        msg = "Invalid input";
                        break;
                    }
                    statement = dbcon.prepareCall("{CALL insert_movie(?, ?, ?, ?, ?)}");
                    statement.setString(1, movie_title);
                    statement.setInt(2, movie_year);
                    statement.setString(3, movie_director);
                    statement.registerOutParameter(4, Types.BOOLEAN);
                    statement.registerOutParameter(5, Types.VARCHAR);
                    statement.execute();
                    Boolean movie_isExist = statement.getBoolean(4);
                    String movie_id = statement.getString(5);
                    if (movie_isExist)
                        msg = String.format("Movie '%s' (id=%s) already exists. ", movie_title, movie_id);
                    else
                        msg = String.format("Movie '%s' added, with id = %s. ", movie_title, movie_id);

                    statement.close();
                    break;
                }
                case "update-rating": {
                    String movie_id = request.getParameter("movie-id");
                    if (movie_id == null || movie_id.length() == 0) {
                        msg = "Invalid input";
                        break;
                    }
                    float movie_rating;
                    int rating_votes;
                    try {
                        movie_rating = Float.parseFloat(request.getParameter("movie-rating"));
                        rating_votes = Integer.parseInt(request.getParameter("rating-votes"));
                    } catch (Exception e) {
                        msg = "Invalid input";
                        break;
                    }
                    statement = dbcon.prepareCall("{CALL update_rating(?, ?, ?, ?)}");
                    statement.setString(1, movie_id);
                    statement.setFloat(2, movie_rating);
                    statement.setInt(3, rating_votes);
                    statement.registerOutParameter(4, Types.INTEGER);

                    statement.execute();

                    int status_code = statement.getInt(4);

                    switch (status_code){
                        case 0:
                            msg = String.format(
                                    "Successfully update movie id=%s with rating=%f and numVotes=%d. ",
                                    movie_id, movie_rating, rating_votes);
                            break;
                        case 1:
                            msg = String.format("Movie (id = %s) does not exist. ", movie_id);
                            break;

                        default:
                            msg = "Error status code from sql procedure. ";
                    }
                    statement.close();
                    break;
                }
                case "link-genre": {
                    String movie_id = request.getParameter("movie-id");
                    String genre_name = request.getParameter("genre");
                    if (movie_id == null || movie_id.length() == 0 || genre_name == null || genre_name.length() == 0) {
                        msg = "Invalid input";
                        break;
                    }
                    statement = dbcon.prepareCall("{CALL link_genre(?, ?, ?)}");
                    statement.setString(1, movie_id);
                    statement.setString(2, genre_name);
                    statement.registerOutParameter(3, Types.INTEGER);

                    statement.execute();

                    int status_code = statement.getInt(3);

                    switch (status_code){
                        case 0:
                            msg = String.format("Successfully link genre %s with movie %s. ", genre_name, movie_id);
                            break;
                        case 1:
                            msg = String.format("Movie (id = %s) does not exist. ", movie_id);
                            break;
                        case 2:
                            msg = String.format("Genre %s already links with movie (id = %s). ", genre_name, movie_id);
                            break;
                        default:
                            msg = "Error status code from sql procedure. ";
                    }
                    statement.close();
                    break;
                }
                case "link-star": {
                    String movie_id = request.getParameter("movie-id");
                    String star_name = request.getParameter("star-name");
                    String star_birthYear = request.getParameter("birth-year");
                    if (movie_id == null || movie_id.length() == 0 || star_name == null || star_name.length() == 0) {
                        msg = "Invalid input";
                        break;
                    }

                    statement = dbcon.prepareCall("{CALL link_star(?, ? ,?, ?)}");
                    statement.setString(1, movie_id);

                    statement.setString(2, star_name);
                    if (star_birthYear == null || star_birthYear.length() == 0)
                        statement.setNull(3, Types.NULL);
                    else
                        statement.setInt(3, Integer.parseInt(star_birthYear));
                    statement.registerOutParameter(4, Types.INTEGER);
                    statement.execute();

                    int status_code = statement.getInt(4);

                    switch (status_code){
                        case 0:
                            msg = String.format("Successfully link star %s with movie %s. ", star_name, movie_id);
                            break;
                        case 1:
                            msg = String.format("Movie (id = %s) does not exist. ", movie_id);
                            break;
                        case 2:
                            msg = String.format("Star %s already links with movie (id = %s). ", star_name, movie_id);
                            break;
                        default:
                            msg = "Error status code from sql procedure. ";

                    }
                    statement.close();
                    break;
                }

                default:
                    msg = "type error";
            }
            responseObj.addProperty("msg", msg);

            response.getWriter().write(responseObj.toString());
            dbcon.close();

        }
        catch (Exception e){
            System.out.println("eee  " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
