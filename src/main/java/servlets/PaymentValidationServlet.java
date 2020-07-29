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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "PaymentValidationServlet", urlPatterns = "/api/payment-validation")
public class PaymentValidationServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb-r")
    public DataSource dataSourceRead;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String cardNumber = request.getParameter("card-number");
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        int month = Integer.parseInt(request.getParameter("month"));
        int year = Integer.parseInt(request.getParameter("year"));

        try{
            Connection dbcon = dataSourceRead.getConnection();
            String query =
                "SELECT COUNT(c.id) AS count " +
                "FROM creditcards AS c " +
                "WHERE c.id = ? AND " +
                "LOWER(c.firstName) = LOWER(?) AND LOWER(c.lastName) = LOWER(?) " +
                "AND MONTH(c.expiration) = ? AND YEAR(c.expiration) = ? ; ";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setInt(4, month);
            statement.setInt(5, year);
            ResultSet rs = statement.executeQuery();

//            Statement statement = dbcon.createStatement();
//            String query =  "SELECT COUNT(c.id) AS count " +
//                            "FROM creditcards AS c " +
//                            "WHERE c.id = '%s' AND " +
//                            "LOWER(c.firstName) = LOWER('%s') AND LOWER(c.lastName) = LOWER('%s') " +
//                            "AND MONTH(c.expiration) = %s AND YEAR(c.expiration) = %s; ";
//            System.out.println(String.format(query, cardNumber, firstName, lastName, month, year));
//            ResultSet rs = statement.executeQuery(String.format(query, cardNumber, firstName, lastName, month, year));
            JsonObject responseJsonObj = new JsonObject();
            if (rs.next()){
                if (rs.getInt("count") > 0){
                    responseJsonObj.addProperty("status", "success");


                }
                else{
                    responseJsonObj.addProperty("status", "fail");
                }
            }
            out.write(responseJsonObj.toString());
            rs.close();
            statement.close();
            out.close();

        }
        catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
