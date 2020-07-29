
let inPageConstrains = "&page=1&page_cap=20&sorting=rating&order=desc";
let titleSearchForm = $("#title-search-form");

$.ajax(
    "api/title-first-letter",
    {
        method: "GET",
        success: fillFirstLetterList
    }
);

$.ajax(
    "api/genres",
    {
        method: "GET",
        success: fillGenreList
    }
);

$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    minChars: 3,
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

$("#search-btn").click(function (e) {
    e.preventDefault();
    if ($("#autocomplete").val().length > 0)
        window.location.href = "movie-list.html?" +titleSearchForm.serialize();
});

$("#advanced-search-btn").click(function(e) {
    e.preventDefault();
    window.location.href = "advanced-search.html";
})

function fillFirstLetterList(firstLetterResult){

    let rowHTML = "";
    for (let i = 0; i < firstLetterResult.length; i++){
        rowHTML += "<a href='movie-list.html?start_with=" + firstLetterResult[i] + inPageConstrains + "'>" + firstLetterResult[i] + "</a> ";
    }
    $("#title-first-letter-list").append(rowHTML);
}

function fillGenreList(genreResult){

    let rowHTML = "";
    for (let i = 0; i < genreResult.length; i++){
        rowHTML += "<a href='movie-list.html?genre=" + genreResult[i] + inPageConstrains + "'>" + genreResult[i] + "</a> ";
    }
    $("#genre-list").append(rowHTML);
}


function handleLookup(query, doneCallback){
    $.ajax({
        "method": "GET",
        "dataType": "JSON",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/title-auto-complete?title=" + escape(query),
        "success": function(JsonData){
            doneCallback({suggestions: JsonData});
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}

function handleSelectSuggestion(suggestion) {
    window.location.replace("single-movie.html?id=" + suggestion["data"]);
}