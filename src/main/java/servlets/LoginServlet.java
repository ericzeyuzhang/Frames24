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

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import models.User;

import reCAPTCHA.RecaptchaVerifyUtils;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/moviedb-r")
	public DataSource dataSourceRead;
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		JsonObject responseJsonObject = new JsonObject();
		try {
			RecaptchaVerifyUtils.verify(gRecaptchaResponse);
		} catch (Exception e) {
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", e.getMessage());
			response.getWriter().write(responseJsonObject.toString());
			return;
		}
//        JsonObject responseJsonObject = new JsonObject();
        try {

        	Connection dbcon = dataSourceRead.getConnection();

        	String query = 	"SELECT c.password, c.firstName " +
        					"FROM customers AS c " + 
        					"WHERE c.email = ?";
        	PreparedStatement statement = dbcon.prepareStatement(query);
        	statement.setString(1, username);
        	ResultSet rs = statement.executeQuery();
        	String correct_password;
        	String firstName;

        	if (!rs.next()) {
        		responseJsonObject.addProperty("status", "fail");
        		responseJsonObject.addProperty("message", "user " + username + " does not exist");        		
        	}
        	else {
        		correct_password = rs.getString("password");
        		firstName = rs.getString("firstName");
        		if (rs.next()) {
            		responseJsonObject.addProperty("status", "fail");
            		responseJsonObject.addProperty("message", "database record error");        			
        		}
        		else {
					if (new StrongPasswordEncryptor().checkPassword(password, correct_password)) {
//            		if (correct_password.equals(password)) {
                        request.getSession().setAttribute("user", new User(username, firstName));

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");        			
            		}
            		else {
                		responseJsonObject.addProperty("status", "fail");
                		responseJsonObject.addProperty("message", "incorrect password");     
            		}
        		}
        	}
        	
        	
        	
        }
        catch (Exception e) {
    		responseJsonObject.addProperty("status", "fail");
    		responseJsonObject.addProperty("message", e.getClass().getName());
    		e.printStackTrace();
//    		response.setStatus(500);
    		
        }
        response.getWriter().write(responseJsonObject.toString());
        
    }
}
