function searchacc(callerid, requestid, dialogid, account) {
    var apiUrl = "https://aknet.softai.kg/customer/search";
    var payload = {
        callerid: callerid,
        requestid: requestid,
        dialogid: dialogid,
        account: account
    };

    log("///1234 Сформированный payload: " + JSON.stringify(payload));

    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    return $http.post(apiUrl, options)
        .then(function(response) {
            log("////---Ответ от сервера: " + JSON.stringify(response));

            var data = response.data;
            if (!data || Object.keys(data).length === 0) {
                log("////---Ничего не найдено по указанному аккаунту.");
                return null;
            }

            // Вернуть первого найденного клиента
            var firstKey = Object.keys(data)[0];
            return data[firstKey];
        })
        .catch(function(error) {
            log("////---Ошибка при запросе: " + JSON.stringify(error));
            return null;
        });
}

function checkDataBase(requestid, dialogid, callerid) {
    var apiUrl = "https://aknet.softai.kg/dialog/get";
    var payload = {
        requestid: requestid,
        dialogid: dialogid,
        callerid: callerid
    };
    log('/////////////////////' + JSON.stringify(payload))
    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    return $http.post(apiUrl, options)
        .then(function(response) {
             log("/// Ответ от сервера: " + JSON.stringify(response));
            return response;
        })
        .catch(function(error) {
            log("/// Ошибка при запросе: " + JSON.stringify(error));
    
            if (error.response) {
                log("/// Код ошибки: " + error.response.status);
                log("/// Ответ от сервера: " + JSON.stringify(error.response.data));
                log("/// Полный ответ: " + JSON.stringify(error.response));
            } else if (error.code === 'ECONNREFUSED') {
                log("Ошибка соединения с сервером");
            } else {
                log("/// Ошибка сети или сервер недоступен");
            }
    
            return null;
        });
}
function endChat(requestid, dialogid,callerid, need_callback,context) {
    var apiUrl = "https://aknet.softai.kg/dialog/add";
    var payload = {
        requestid: requestid,
        dialogid: dialogid,
        callerid: callerid,
        need_callback: need_callback,
        context:context
    };
    log('/////////////////////' + payload)
    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    return $http.post(apiUrl, options)
        .then(function(response) {
             log("/// Ответ от сервера: " + JSON.stringify(response));
            return response;
        })
        .catch(function(error) {
            log("/// Ошибка при запросе: " + JSON.stringify(error));
    
            if (error.response) {
                log("/// Код ошибки: " + error.response.status);
                log("/// Ответ от сервера: " + JSON.stringify(error.response.data));
                log("/// Полный ответ: " + JSON.stringify(error.response));
            } else if (error.code === 'ECONNREFUSED') {
                log("Ошибка соединения с сервером");
            } else {
                log("/// Ошибка сети или сервер недоступен");
            }
    
            return null;
        });
}
