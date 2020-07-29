package servlets;

import com.google.gson.JsonObject;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginStatusServlet", urlPatterns = "/api/login-status")
public class LoginStatusServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        JsonObject responseObj = new JsonObject();
        if (user == null) {
            responseObj.addProperty("status", "0");
            responseObj.addProperty("first_name", "0");
        }
        else {
            responseObj.addProperty("status", "1");
            responseObj.addProperty("first_name", user.getFirstName());
        }
        response.getWriter().write(responseObj.toString());
    }
}
