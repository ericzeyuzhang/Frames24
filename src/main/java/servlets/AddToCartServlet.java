package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import com.google.gson.JsonObject;
import models.User;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/add-to-cart")
public class AddToCartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String movieName = request.getParameter("movie-name");
        String quantity = request.getParameter("quantity");
//        String isResponseNeeded = request.getParameter("response");
        User user = (User) session.getAttribute("user");
        if (movieName != null){
            synchronized (user) {
                if (quantity == null) {
                    user.addToCart(movieName);
                }
                else {
                    user.setCartQuantity(movieName, Integer.parseInt(quantity));
                }
            }
        }

        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("status", "success");
        responseObj.add("cart_info", user.getCartInfo());
        response.getWriter().write(responseObj.toString());

    }
}
