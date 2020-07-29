package servlets;

import com.google.gson.JsonArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import models.User;

@WebServlet(name = "CartInfoServlet", urlPatterns = "/api/cart-info")
public class CartInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        JsonArray cartArr = new JsonArray();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        response.getWriter().write(user.getCartInfo().toString());
    }
}
