theme: /Auth
    
    state:Authorization
        script: $session.stateCounterInARow = 0
            
        if:$client.userInfo
            a:В прошлый раз Вы авторизовались по адресу address, продолжаем говорить про этот адрес?
        else:
            go!:/Auth/AskPhone
            
        state:ThisAddress
            q: * $agree *
            a:Отлично, тогда продолжим.
        
        state:AskPhone
            q: * $disagree *
            go!:/Auth/AskPhone
            
        state: CatchAll || noContext = true
            event: noMatch
    
            script:
                $session.stateCounterInARow++
    
            if: $session.stateCounterInARow < 2
                a:Извините, не понял Вас. Подскажите, вы обращаетесь по поводу адреса {{address}}?
            else:
                a:go! /СonnectionTechSupport
                
    state:AskPhone
        script: $session.stateCounterInARow = 0
        
        if:$session.callerid
            a:Подскажите, вы хотите авторизоваться по номеру номер из $request?
        else:
            a:Для продолжения разговора мне нужно Вас авторизовать. Укажите, пожалуйста, номер телефона, привязанный к счёту.
        
        state: ThisPhone
            q: * $agree *
            go!:/Auth/AskAccountNumber
            
        state:AnotherPhone
            q: * $disagree *
            script:
                delete $session.callerid
            go!:/Auth/AskPhone
            
        state:SavePhone
            q: * @duckling.phone-number *
            script:
                $session.callerid = $parseTree["_duckling.phone-number"]
            go!:/Auth/AskAccountNumber
            
        state: CatchAll || noContext = true
            event: noMatch
    
            script:
                $session.stateCounterInARow++
    
            if: $session.stateCounterInARow < 2
                a:Извините, для продолжения авторизации мне необходим Ваш номер телефона. Пожалуйста, укажите его в международном формате.
            else:
                a:go! /СonnectionTechSupport
                
            
    
    state:AskAccountNumber
        a:Спасибо! Теперь сообщите, пожалуйста, номер Вашего лицевого счёта. Он состоит из 2-10 цифр, и посмотреть его можно в портале iPTV приставки, либо в договоре, заключённом с компанией.
        buttons:
            "Я не знаю/не помню свой ЛС" -" -> /Auth/SearchByPhone
            
        state: ConfirmAddress
            q: * @duckling.number *
            script:
                $session.account = $parseTree["_duckling.number"];
                var callerid = $session.callerid;
                var requestid = $context.request.questionId;
                var dialogid = $context.sessionId;
                var account = $session.account;
            
                searchacc(callerid, requestid, dialogid, account).then(function(result) {
                    if (!result || result.status === "error") {
                        log("Ошибка при выполнении запроса.");
                        $reactions.transition("/Auth/AskAccountNumber/ConfirmAddress/Error");
                        return;
                    }
            
                    if (result.status === "empty") {
                        log("Нет данных для отображения.");
                        $reactions.answer("Такой счёт в базе не нашёлся.");
                        $reactions.transition("/Auth/SearchByPhone");
                        return;
                    }
            
                    $session.searchResults = result.customers;
                    log("//// RESULTS1 " + toPrettyString($session.searchResults));
            
                    var output = AdressesOutput($session.searchResults);
                    log("//// result " + toPrettyString(output));
            
                    $reactions.answer("В базе указан адрес " + output.addresses + ". Данная информация актуальна?");
                    $reactions.buttons(output.buttons);
                });
            
            state:Agree
                q: * $agree *
                go!:/Auth/SuccessfulAuthorization
                    
            state:Disagree
                q: * $disagree *
                go!:/Auth/SearchByPhone
                
            state:Error
                script:
                    $session.stateCounterInARow++
        
                if: $session.stateCounterInARow < 2
                    go!:/Auth/AskAccountNumber/ConfirmAddress
                else:
                    a:go! /AnyError
                    
        state:CatchAll || noContext = true
            script:
                $session.catchStateCounterInARow++
    
            if: $session.catchStateCounterInARow < 2
                a:Извините, похоже, при вводе номера была допущена ошибка. Пожалуйста, перепроверьте номер лицевого счёта и введите его ещё раз. Номер должен состоять из 2-10 цифр, посмотреть его можно в портале iPTV приставки, либо в договоре, заключённом с компанией.
            else:
                a:go! /ConnectionTechSupport
        
    state:SearchByPhone
        a:Попробую поискать подходящие счета по номеру телефона.
        script:
            $session.account = $parseTree["_duckling.number"];
            var callerid = $session.callerid;
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
        
            searchacc(callerid, requestid, dialogid).then(function(result) {
                if (!result || result.status === "error") {
                    log("Ошибка при выполнении запроса.");
                    $reactions.transition("/Auth/SearchByPhone/Error");
                    return;
                }
        
                if (result.status === "empty") {
                    log("Нет данных для отображения.");
                    $reactions.answer("Такой счёт в базе не нашёлся.");
                    $reactions.answer("go! /ConnectionTechSupport");
                    return;
                }
        
                $session.searchResults = result.customers;
                log("//// RESULTS1 " + toPrettyString($session.searchResults));
        
                var output = AdressesOutput($session.searchResults);
                log("//// result " + toPrettyString(output));
        
                if (Array.isArray(output.addresses)) {
                    $reactions.answer("Выберите нужный адрес из списка:");
                    $reactions.buttons(output.buttons);
                    $reactions.buttons({ text: "Нет нужного адреса", transition: "/Auth/SearchByName" });
                } else {
                    $session.account = result.customers[0].customer.id;
                    $reactions.answer("В базе указан адрес " + output.addresses + ". Данная информация актуальна?");
                    $reactions.buttons(output.buttons);
                }
            });
        
        state:Agree
            q: * $agree *
            go!:/Auth/SuccessfulAuthorization
                
        state:Disagree
            q: * $disagree *
            script: delete $session.account
            go!:/Auth/SearchByName
            
        state:Error
            script:
                $session.stateCounterInARow++
    
            if: $session.stateCounterInARow < 2
                go!:/Auth/SearchByPhone
            else:
                a:go! /AnyError
            
        
    state:SearchByName
        a:Пожалуйста, уточните полные фамилию, имя и отчество владельца договора.
            
        state:DontKnow
            q: * $dontKnow *
            a:go! /СonnectionTechSupport
            
        state:CheckName
            event: noMatch
            script:
                var name = $request.query
                var callerid = $session.callerid;
                var requestid = $context.request.questionId;
                var dialogid = $context.sessionId;
                $session.name = name
            
                searchacc(callerid, requestid, dialogid, undefined, name).then(function(result) {
                    if (!result || result.status === "error") {
                        log("Ошибка при выполнении запроса.");
                        $reactions.transition("/Auth/SearchByName/CheckName/Error");
                        return;
                    }
            
                    if (result.status === "empty") {
                        log("Нет данных для отображения.");
                        $reactions.answer("Такой счёт в базе не нашёлся.");
                        $reactions.answer("go! /ConnectionTechSupport");
                        return;
                    }
            
                    $session.searchResults = result.customers;
                    log("//// RESULTS1 " + toPrettyString($session.searchResults));
            
                    var output = AdressesOutput($session.searchResults);
                    log("//// result " + toPrettyString(output));
            
                    if (Array.isArray(output.addresses)) {
                        $reactions.answer("Выберите нужный адрес из списка:");
                        $reactions.buttons(output.buttons);
                        $reactions.buttons({ text: "Нет нужного адреса", transition: "/Auth/SearchByName" });
                    } else {
                        $session.account = result.customers[0].customer.id;
                        $reactions.answer("В базе указан адрес " + output.addresses + ". Данная информация актуальна?");
                        $reactions.buttons(output.buttons);
                    }
                });
                
            state:Agree
                q: * $agree *
                go!:/Auth/SuccessfulAuthorization
                    
            state:Disagree
                q: * $disagree *
                script: delete $session.account
                go!:/Auth/SearchByAddress
                
            state:Error
                script:
                    $session.stateCounterInARow++
        
                if: $session.stateCounterInARow < 2
                    go!:/Auth/SearchByName/CheckName
                else:
                    a:go! /AnyError
                

        script:
            
    
    state:SearchByAddress
        a:Пожалуйста, введите полный адрес владельца договора.
            
        state:DontKnow
            q: * $dontKnow *
            a:go! /СonnectionTechSupport
            
        state:CheckAddress
            event: noMatch
            script:
                var address = $request.query
                var name = $session.name
                var callerid = $session.callerid;
                var requestid = $context.request.questionId;
                var dialogid = $context.sessionId;
            
                searchacc(callerid, requestid, dialogid, undefined, name, address).then(function(result) {
                    if (!result || result.status === "error") {
                        log("Ошибка при выполнении запроса.");
                        $reactions.transition("/Auth/SearchByPhone/Error");
                        return;
                    }
            
                    if (result.status === "empty") {
                        log("Нет данных для отображения.");
                        $reactions.answer("Такой счёт в базе не нашёлся.");
                        $reactions.answer("go! /ConnectionTechSupport");
                        return;
                    }
            
                    $session.searchResults = result.customers;
                    log("//// RESULTS1 " + toPrettyString($session.searchResults));
            
                    var output = AdressesOutput($session.searchResults);
                    log("//// result " + toPrettyString(output));
            
                    if (Array.isArray(output.addresses)) {
                        $reactions.answer("Выберите нужный адрес из списка:");
                        $reactions.buttons(output.buttons);
                        $reactions.buttons({ text: "Нет нужного адреса", transition: "/Auth/SearchByName" });
                    } else {
                        $session.account = result.customers[0].customer.id;
                        $reactions.answer("В базе указан адрес " + output.addresses + ". Данная информация актуальна?");
                        $reactions.buttons(output.buttons);
                    }
                });
                
            state:Agree
                q: * $agree *
                go!:/Auth/SuccessfulAuthorization
                    
            state:Disagree
                q: * $disagree *
                script: delete $session.account
                go!:/Auth/SearchByAddress
                
            state:Error
                script:
                    $session.stateCounterInARow++
        
                if: $session.stateCounterInARow < 2
                    go!:/Auth/SearchByAddress/CheckAddress
                else:
                    a:go! /AnyError
        
    state:SuccessfulAuthorization
        script:
            $session.userAuthorized = true;
                
            if($session.purpose == "$CheckBalance"){
                $reactions.transition("/CheckBalance/Balance");
            }if($session.purpose == "$GetCredit"){
                $reactions.transition("/Credit/TemporaryAccess");
            }if($session.purpose == "$NoInternetOrTV"){
                $reactions.transition("/Status/ApplicationStatus");
            }




