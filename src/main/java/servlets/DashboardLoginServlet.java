package servlets;

import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;
import reCAPTCHA.RecaptchaVerifyUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/api/dashboard-login")
public class DashboardLoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
	private static final long serialVersionUID = 1L;

	@Resource(name = "jdbc/moviedb-r")
	public DataSource dataSourceRead;
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
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

        	String query = 	"SELECT e.password " +
        					"FROM employees AS e " +
        					"WHERE e.email = ?";
        	PreparedStatement statement = dbcon.prepareStatement(query);
        	statement.setString(1, email);
        	ResultSet rs = statement.executeQuery();
        	String correct_password;

        	if (!rs.next()) {
        		responseJsonObject.addProperty("status", "fail");
        		responseJsonObject.addProperty("message", "employee " + email + " does not exist");
        	}
        	else {
        		correct_password = rs.getString("password");
        		if (rs.next()) {
            		responseJsonObject.addProperty("status", "fail");
            		responseJsonObject.addProperty("message", "database record error");        			
        		}
        		else {
					if (new StrongPasswordEncryptor().checkPassword(password, correct_password)) {
//            		if (correct_password.equals(password)) {
                        request.getSession().setAttribute("employee", email);

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
    		responseJsonObject.addProperty("message", e.getMessage());    
    		response.setStatus(500);
    		
        }
        response.getWriter().write(responseJsonObject.toString());
        
    }
}
