navUserInfo = $(".nav-user");
navSignOutBtn = $("#sign-out-btn");
$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/login-status",
    success: function (data) {
        console.log(data);
        if (data["status"] == "1"){
            navUserInfo.html("Hi, " + data["first_name"]);

        }
        else {
            navUserInfo.html("Sign-In");
            navUserInfo.attr("href", "login.html");

        }
    }
});

navSignOutBtn.click(function (e) {
    $.ajax({
        // dataType: "json",
        method: "GET",
        url: "api/sign-out",
    });
});