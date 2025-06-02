function searchacc(callerid, requestid, dialogid, account) {
    var apiUrl = "https://aknet.softai.kg/customer/search";
    var payload = {
        callerid: callerid,
        requestid: requestid,
        dialogid: dialogid,
        account: account
    };
    
    log("/// Сформированный payload: " + JSON.stringify(payload));
    
    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    return $http.post(apiUrl, options)
        .then(function(response) {
            log("Ответ от сервера: " + JSON.stringify(response));
            return response;
        })
        .catch(function(error) {
            log("Ошибка при запросе:", error);
            return null;
        }); 
}
