function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");
    $("title, h1").html(resultData[0]["star_name"]);

    let starInfoElement = $("#star-info");

   starInfoElement.html("Birth Year: " + resultData[0]["star_dob"]);

    let movieTableBodyElement = $("#movie-table-body");

    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th><a href=single-movie.html?id=" + resultData[i]["movie_id"] + ">" + resultData[i]["movie_title"] + "</a></th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<td><a href='#' class='add-to-cart-btn'><img src='resources/images/cart.png' width='40' height='40'></img></a></td>";

        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
    let addToCartBtns = $(".add-to-cart-btn");
    addToCartBtns.on("click", function(e){
        e.preventDefault();
        let movieName = resultData[addToCartBtns.index(this)]["movie_title"];
        $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/add-to-cart?movie-name=" + movieName,
            success: function (response) {
                if (response["status"] == "redirect"){
                    window.location.href = "login.html";
                }
                if (response["status"] == "success"){
                    alert("Added '" + movieName + "' to cart.");
                }
            }
        });
    });
}


// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

let prevQuery = localStorage.getItem("prev_query");

if (prevQuery && prevQuery.length > 0) {
    $("#back-btn").click(function () {
        window.location.replace(prevQuery);
    });
}
else {
    $("#back-btn").prop('disabled', true);
}