
/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

    let movieInfoElement = $("#movie-info");

    // let movieTitle = resultData["movie_title"];
    $("title, h1").html(resultData["movie_title"]);
    let addToCartBtns = $(".add-to-cart-btn");
    addToCartBtns.on("click", function(e){
        e.preventDefault();
        let movieName = resultData["movie_title"];
        $.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
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

    })

    movieInfoElement.html (

                            "Year: " + resultData["movie_year"] + "<br>" +
                            "Director: " + resultData["movie_director"] + "<br>" +
                            "Rating: " + resultData["movie_rating"] + "<br>" +
                            "Genres: " + resultData["movie_genres"]
                            );

    let starTableBodyElement = $("#star-table-body");

    for (let i = 0; i < resultData["movie_stars"].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<th><a href=single-star.html?id=" + resultData["movie_stars"][i]["id"] + ">" + resultData["movie_stars"][i]["name"] + "</a></th>";
        rowHTML += "<th>" + resultData["movie_stars"][i]["birth_year"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
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
