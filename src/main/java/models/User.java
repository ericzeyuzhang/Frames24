package models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */

public class User {

    private final String username;
    private final String firstName;
    private String prevSearchQuery;
    private HashMap<String, Integer> cart;

    public User(String username, String firstName) {
        this.username = username;
        this.firstName = firstName;
        this.cart = new HashMap<>();
        this.prevSearchQuery = null;
    }

    private String getPrevSearch(){
        return this.prevSearchQuery;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void addToCart(String movieName){
        Integer count = this.cart.get(movieName);
        if (count == null){
            this.cart.put(movieName, 1);
        }
        else {
            this.cart.put(movieName, count + 1);
        }
    }

    public void removeFromCart(String movieName){
        Integer count = this.cart.get(movieName);
        if (count != null) {
            if (count > 1){
                this.cart.put(movieName, count - 1);
            }
            else {
                this.cart.remove(movieName);
            }
        }
    }

    public void setCartQuantity(String movieName, int quantity){
        if (quantity <= 0){
            this.removeFromCart(movieName);
        }
        else {
            this.cart.put(movieName, quantity);
        }
    }

    public JsonArray getCartInfo(){
        JsonArray movieArr = new JsonArray();
        Iterator iterator = this.cart.keySet().iterator();
        while (iterator.hasNext()){
            JsonObject singleMovieObj = new JsonObject();
            String key = (String) iterator.next();
            singleMovieObj.addProperty("name", key);
            singleMovieObj.addProperty("quantity", this.cart.get(key));
            movieArr.add(singleMovieObj);
        }
        return movieArr;

    }

    public void emptyCart(){
        this.cart.clear();
    }

}
