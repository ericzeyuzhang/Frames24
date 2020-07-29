let starForm = $("#star-form");
let genreForm = $("#genre-form");
let movieForm = $("#movie-form");
let updateRatingForm = $("#update-rating-form")
let linkGenreForm = $("#link-genre-form");
let linkStarForm = $("#link-star-form");

starForm.submit(function (e) {
    e.preventDefault();
    let boxObj = $(".msg-box", this);
    $.ajax({
        url: "api/dashboard",
        dataType: "JSON",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: "type=star&" + starForm.serialize(),
        success: (data) => handleErrMsg(data, boxObj)
    });
});

genreForm.submit(function (e) {
    e.preventDefault();
    let boxObj = $(".msg-box", this);
    $.ajax({
        url: "api/dashboard",
        dataType: "JSON",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: "type=genre&" + genreForm.serialize(),
        success: (data) => handleErrMsg(data, boxObj)
    });
});

movieForm.submit(function (e) {
    e.preventDefault();
    let boxObj = $(".msg-box", this);
    $.ajax({
        url: "api/dashboard",
        dataType: "JSON",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: "type=movie&" + movieForm.serialize(),
        success: (data) => handleErrMsg(data, boxObj)
    });
});

updateRatingForm.submit(function (e) {
    e.preventDefault();
    let boxObj = $(".msg-box", this);
    $.ajax({
        url: "api/dashboard",
        dataType: "JSON",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: "type=update-rating&" + updateRatingForm.serialize(),
        success: (data) => handleErrMsg(data, boxObj)
    });
});

linkGenreForm.submit(function (e) {
    e.preventDefault();
    let boxObj = $(".msg-box", this);
    $.ajax({
        url: "api/dashboard",
        dataType: "JSON",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: "type=link-genre&" + linkGenreForm.serialize(),
        success: (data) => handleErrMsg(data, boxObj)
    });
});

linkStarForm.submit(function (e) {
    e.preventDefault();
    let boxObj = $(".msg-box", this);
    $.ajax({
        url: "api/dashboard",
        dataType: "JSON",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        data: "type=link-star&" + linkStarForm.serialize(),
        success: (data) => handleErrMsg(data, boxObj)
    });
});


function handleErrMsg(responseJson, boxObj) {
    boxObj.html(responseJson["msg"]);
}


linkStarForm.submit(function (e) {
    e.preventDefault();
    console.log($(this).serialize());
});



