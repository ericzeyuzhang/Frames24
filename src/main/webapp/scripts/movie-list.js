let pageCap;
let currPage;
let sorting;
let order;

init();

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



function replaceUrlParamVal(url, paramName, replaceWith) {
    let re = eval('/('+ paramName+'=)([^&]*)/gi');
    let nUrl = url.replace(re, paramName+'='+replaceWith);
    return nUrl;
}

function requestUpdate(){
    let currURL = window.location.href;
    currURL = replaceUrlParamVal(currURL, "page", currPage);
    currURL = replaceUrlParamVal(currURL, "page_cap", pageCap);
    currURL = replaceUrlParamVal(currURL, "sorting", sorting);
    currURL = replaceUrlParamVal(currURL, "order", order);

    window.history.pushState(null, null, currURL);
    localStorage.setItem("prev_query", currURL);
    let currQueries = "";
    let idx = currURL.indexOf("?");

    if (idx != -1){
        currQueries = currURL.substring(idx + 1);
    }
    $.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movie-list?" + currQueries,
        success: (resultData) => handleMoviesResult(resultData)
    });
}

function init() {
    pageCap = parseInt(getParameterByName("page_cap"));
    currPage = parseInt(getParameterByName("page"));
    sorting = getParameterByName("sorting");
    order = getParameterByName("order");
    let indicator = "";
    if (order == "desc") indicator = "↓";
    else indicator = "↑";
    $("#" + sorting + "-sort-btn .order-indicator").html(indicator);
    $("#page_cap_selector_bar").find("[title=" + pageCap + "]").addClass("selected");
    $("#prev_page_btn").click(function(){
        currPage--;
        requestUpdate();
    });

    $("#next_page_btn").click(function(){
        currPage++;
        requestUpdate();
    });
    $("#page_cap_selector_bar li").click(handlePageCapSelection);
    $("thead .sortable").click(handleSortRequest);
    requestUpdate();
}


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;

    let movieTableBodyElement = $("#movie_table_body");
    let pageSelectorBarElement = $("#page_selector_bar");
    movieTableBodyElement.empty();
    pageSelectorBarElement.empty();

    let numResult = parseInt(resultData["num_result"]);
    let numPage = Math.ceil(numResult / pageCap);
    currPage = Math.min(currPage, numPage);
    if (numPage == 0){
        movieTableBodyElement.append("<tr><td>No results found.</td></tr>");
        return ;
    }
    console.log(currPage, numPage);
    if (currPage != 1 && currPage != numPage){
        $("#prev_page_btn").css("visibility", "visible");
        $("#next_page_btn").css("visibility", "visible");
    }
    else if (currPage != numPage){
        $("#prev_page_btn").css("visibility", "hidden");
        $("#next_page_btn").css("visibility", "visible");
    }
    else if (currPage != 1){
        $("#prev_page_btn").css("visibility", "visible");
        $("#next_page_btn").css("visibility", "hidden");
    }
    else {
        $("#prev_page_btn").css("visibility", "hidden");
        $("#next_page_btn").css("visibility", "hidden");
    }
    let rowHTML = "";


    let startPageNum = Math.max(1, Math.min(currPage - 3, numPage - 7));
    for (let i = startPageNum; i <= Math.min(numPage, startPageNum + 7); i++){
        if (i != currPage){
            rowHTML += "<li>" + i + "</li>";
        }
        else{
            rowHTML += "<li class='selected'>" + i + "</li>";
        }

    }
    pageSelectorBarElement.append(rowHTML);
    // pageSelectorBarElement.children().eq(currPage - 1).addClass("selected");
    let pageSelectorElement = pageSelectorBarElement.children();
    pageSelectorElement.click(function(e){
        e.preventDefault();
        currPage = parseInt($(this).html());
        if($(this).attr("class") != "selected"){
            requestUpdate();
        }

    });

    for (let i = 0; i < resultData["movie_list"].length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";

        //rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<td><a href=single-movie.html?id=" + resultData["movie_list"][i]["movie_id"] + ">" + resultData["movie_list"][i]["movie_title"] + "</a></td>";
        rowHTML += "<td>" + resultData["movie_list"][i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData["movie_list"][i]["movie_director"] + "</td>";
        rowHTML += "<td>" + resultData["movie_list"][i]["movie_rating"] + "</td>";
        rowHTML += "<td>" + resultData["movie_list"][i]["movie_genres"] + "</td>";
        rowHTML += "<td>";
        let starList = resultData["movie_list"][i]["movie_stars"];
        for (let j = 0; j < starList.length; j++){
            rowHTML += "<a href=single-star.html?id=" + starList[j]["id"] + ">" + starList[j]["name"] + "</a>" + "<br>";
        }
        rowHTML += "</td>";

        rowHTML += "<td><a href='#' class='add-to-cart-btn'><img src='resources/images/cart.png' width='40' height='40'></img></a></td>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    let addToCartBtns = $(".add-to-cart-btn");
    addToCartBtns.on("click", function(e){
        e.preventDefault();
        let movieName = resultData["movie_list"][addToCartBtns.index(this)]["movie_title"];
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
}

function handlePageCapSelection(e) {
    if (!$(this).hasClass("selected")) {
        $(this).addClass("selected");
        $(this).siblings().removeClass("selected");
        pageCap = parseInt($(this).attr("title"));
        requestUpdate();
    }
}


function handleSortRequest(e){
    sorting = $(".col-name", this).html().toLowerCase();
    if ($(this).hasClass("sorted")){
        if ($(this).hasClass("desc")){
            $(this).removeClass("desc").addClass("asc");
            order = "asc";
        }
        else{
            $(this).removeClass("asc").addClass("desc");
            order = "desc";
        }
    }
    else{
        $(this).addClass("sorted").addClass("desc");
        $(this).siblings().removeClass("sorted").removeClass("desc").removeClass("asc");
        order = "desc";
    }
    $("thead .order-indicator").html("");
    $("thead .asc .order-indicator").html("↑");
    $("thead .desc .order-indicator").html("↓");
    requestUpdate();
}





