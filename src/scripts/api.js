function searchacc(callerid, requestid, dialogid, account, name) {
    var apiUrl = "https://aknet.softai.kg/customer/search";
    var payload = {
        callerid: callerid,
        requestid: requestid,
        dialogid: dialogid
    };

    if (account !== undefined) {
        payload.account = account;
    }
    if (name !== undefined) {
        payload.name = name;
    }

    log("///1234 Сформированный payload: " + JSON.stringify(payload));

    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    return $http.post(apiUrl, options)
        .then(function(response) {
            var data = response.data;

            if (!data || Object.keys(data).length === 0) {
                log("////---Пустой результат от API");
                return { status: "empty", customers: [] };
            }

            var customers = Object.keys(data).map(function(key) {
                var entry = data[key];
                entry.id = parseInt(key);
                return entry;
            });

            return { status: "ok", customers: customers };
        })
        .catch(function(error) {
            log("////---Ошибка при запросе: " + JSON.stringify(error));
            return { status: "error", message: error };
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

function commonprob(requestid, dialogid) {
    var apiUrl = "https://testbot.softai.kg/probe/common";
    var payload = {
        requestid: requestid,
        dialogid: dialogid
    };

    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    log("URL запроса: " + apiUrl);
    log("Заголовки запроса: " + JSON.stringify(options.headers));
    log("Тело запроса: " + options.body);

    return $http.post(apiUrl, options)
        .then(function(response) {
            log("Ответ от сервера: " + JSON.stringify(response));
            return response.data; // Важно: возвращаем только нужные данные
        })
        .catch(function(error) {
            log("/// Ошибка при запросе: " + JSON.stringify(error));

            if (error.response) {
                log("/// Код ошибки: " + error.response.status);
                log("/// Ответ от сервера: " + JSON.stringify(error.response.data));
            } else if (error.code === 'ECONNREFUSED') {
                log("Ошибка соединения с сервером");
            } else {
                log("/// Ошибка сети или сервер недоступен");
            }

            return null; // Возвращаем null в случае ошибки
        });
}

function getCustomerInfo(requestid, dialogid, callerid, account) {
    var apiUrl = "https://testbot.softai.kg/customer/get";
    var payload = {
        requestid: requestid,
        dialogid: dialogid,
        callerid: callerid,
        account: account
    };

    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    log("URL запроса: " + apiUrl);
    log("Заголовки запроса: " + JSON.stringify(options.headers));
    log("Тело запроса: " + options.body);

    return $http.post(apiUrl, options)
        .then(function(response) {
            log("Ответ от customer/get: " + JSON.stringify(response));
            return response.data; // возвращаем customer
        })
        .catch(function(error) {
            log("Ошибка запроса customer/get: " + JSON.stringify(error));

            if (error.response) {
                log("Код ошибки: " + error.response.status);
                log("Ответ: " + JSON.stringify(error.response.data));
            } else if (error.code === 'ECONNREFUSED') {
                log("Ошибка соединения с сервером");
            } else {
                log("Сетевая ошибка или сервер недоступен");
            }

            return null;
        });
}

function fetchBalanceCustomerData(requestid, dialogid, callerid, account, address) {
    var apiUrl = "https://testbot.softai.kg/customer/get";
    var payload = {
        requestid: requestid,
        dialogid: dialogid,
        callerid: callerid,
        account: account,
        address: address
    };

    var options = {
        body: JSON.stringify(payload),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    log("URL запроса: " + apiUrl);
    log("Заголовки запроса: " + JSON.stringify(options.headers));
    log("Тело запроса: " + options.body);

    return $http.post(apiUrl, options)
        .then(function(response) {
            log("Ответ от customer/get: " + JSON.stringify(response));
            return response.data; // возвращаем customer
        })
        .catch(function(error) {
            log("Ошибка запроса customer/get: " + JSON.stringify(error));

            if (error.response) {
                log("Код ошибки: " + error.response.status);
                log("Ответ: " + JSON.stringify(error.response.data));
            } else if (error.code === 'ECONNREFUSED') {
                log("Ошибка соединения с сервером");
            } else {
                log("Сетевая ошибка или сервер недоступен");
            }

            return null;
        });
}
