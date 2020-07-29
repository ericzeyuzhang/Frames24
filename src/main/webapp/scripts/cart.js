let cartTableBody = $("#cart-table-body");

// function updateCart(){
//
// }

function handleCartInfo(resultData){
    cartTableBody.empty();
    let rowHTML = "";
    let cartInfo = resultData["cart_info"];
    for (let i = 0; i < cartInfo.length; i++){
        rowHTML += "<tr>";
        rowHTML += "<td>" + cartInfo[i]["name"] + "</td>";
        // rowHTML += "<td>" + resultData[i]["quantity"] + "</td>";
        rowHTML += "<td style='text-align: center'><input type='number' min='0' value='" + cartInfo[i]["quantity"] + "' class='qty-input'></input></td>";
        rowHTML += "</tr>";
    }
    cartTableBody.append(rowHTML);
    let qtyInputClass = $(".qty-input");
    qtyInputClass.on("keyup", function (e) {
        $(this).val($(this).val().replace(/\D|^0/g,''));
    });
    qtyInputClass.on("blur", function (e) {
        let qty = $(this).val();
        if (qty.length == 0){
            $(this).val("1");
        }
        if (qty == "0"){
            alert("Remove movie.");
        }

        $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/add-to-cart?movie-name=" + $(this).parent().prev().html() + "&quantity=" + qty,
            success: (resultData) => handleCartInfo(resultData)
        });
        
    });
}


$.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/add-to-cart",
    success: (resultData) => handleCartInfo(resultData)
});

$("#checkout-btn").click(function () {
    window.location.replace("checkout.html");
})