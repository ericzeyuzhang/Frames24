let orderSummaryTable = $("#order-summary-table-body");

function handleOrderSummary(orderSummaryData){
    let rowHTML = "";
    for(let i = 0; i < orderSummaryData.length; i++){
        rowHTML += "<tr>";
        rowHTML += "<td>" + orderSummaryData[i]["name"] + "</td>";
        rowHTML += "<td style='text-align: center'>" + orderSummaryData[i]["quantity"] + "</td>";
        rowHTML += "</tr>";
    }
    orderSummaryTable.append(rowHTML);
}

function handleValidationResult(validationResult){
    let status = validationResult["status"];
    if (status == "success"){
        window.location.replace("order-confirm.html");
    }
    else {
        console.log("test");
        $("#payment-err-msg").html("No records found.");
    }
}


$("#payment-submit-btn").click(function(e){
    e.preventDefault();
    $.ajax({
        dataType: "JSON",
        method: "POST",
        url: "api/payment-validation",
        data: $("#payment-form").serialize(),
        success: (validationResult) => handleValidationResult(validationResult)
    });
});


$.ajax({
    dataType: "JSON",
    method: "GET",
    url: "api/cart-info",
    success: (orderSummaryData) => handleOrderSummary(orderSummaryData)
});