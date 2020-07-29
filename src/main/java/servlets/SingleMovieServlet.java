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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//import java.io.IOException;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SingleMovieServlet
 */
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	@Resource(name = "jdbc/moviedb-r")
	public DataSource dataSourceRead;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String movieId = request.getParameter("id");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSourceRead.getConnection();

			String Query =  "SELECT " +
					"m.title AS movie_title, m.year AS movie_year, m.director AS movie_director, " +
					"GROUP_CONCAT(DISTINCT g.name SEPARATOR ', ') AS movie_genres " +
					"FROM " +
					"movies AS m, genres_in_movies AS gim, genres AS g " +
					"WHERE " +
					"gim.movieId = m.id AND gim.genreId = g.id AND m.id = ? " +
					"GROUP BY m.id; ";

			String ratingQuery =
					"SELECT r.rating FROM ratings AS r WHERE movieId = ? ;";
			String starQuery =
					"SELECT s.id, s.name, s.birthYear " +
					"FROM stars AS s, movies AS m, stars_in_movies AS sim " +
					"WHERE sim.starId = s.id AND sim.movieId = m.id AND m.id = ? ;";
			PreparedStatement stmt = dbcon.prepareStatement(Query);
			PreparedStatement ratingStmt = dbcon.prepareStatement(ratingQuery);
			PreparedStatement starStmt = dbcon.prepareStatement(starQuery);
			System.out.println(String.format(Query, movieId));
			// Perform the query
			stmt.setString(1, movieId);
			ResultSet rs = stmt.executeQuery();

			JsonObject singleMovieObject = new JsonObject();
			if (rs.next()){
				String movieTitle = rs.getString("movie_title");
				String movieYear = rs.getString("movie_year");
				String movieDirector = rs.getString("movie_director");
				String movieGenres = rs.getString("movie_genres");

				singleMovieObject.addProperty("movie_title", movieTitle);
				singleMovieObject.addProperty("movie_year", movieYear);
				singleMovieObject.addProperty("movie_director", movieDirector);
				singleMovieObject.addProperty("movie_genres", movieGenres);
			}
			rs.close();
			stmt.close();
			ratingStmt.setString(1, movieId);
			rs = ratingStmt.executeQuery();
			String movieRating;
			if (rs.next()){
				movieRating = rs.getString("rating");
			}
			else{
				movieRating = "N/A";
			}
			singleMovieObject.addProperty("movie_rating", movieRating);

			rs.close();
			ratingStmt.close();

			starStmt.setString(1, movieId);
			rs = starStmt.executeQuery();
			JsonArray movieStarsArr = new JsonArray();
			while (rs.next()){
				JsonObject singleStarObj = new JsonObject();
				singleStarObj.addProperty("id", rs.getString("id"));
				singleStarObj.addProperty("name", rs.getString("name"));
				singleStarObj.addProperty("birth_year", rs.getString("birthYear"));
				movieStarsArr.add(singleStarObj);
			}
			singleMovieObject.add("movie_stars", movieStarsArr);

			rs.close();
			starStmt.close();
            // write JSON string to output
            out.write(singleMovieObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
