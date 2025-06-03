theme: /Auth
    
    state:Authorization
        script: $session.stateCounterInARow = 0
            $session.callerid = "776123456"
            
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
            q: * agree *
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
                var callerid = $session.callerid
                var requestid = $context.request.questionId;
                var dialogid = $context.sessionId;
                var account = $session.account;
        
                searchacc(callerid, requestid, dialogid, account).then(function(results) {
                    if (!results) {
                        log("Ошибка при выполнении запроса.");
                        $temp.Error = true;
                        return;
                    }
        
                    if (Object.prototype.toString.call(results) === '[object Array]' && results.length === 0) {
                        log("Нет данных для отображения.");
                        $temp.noResults = true;
                        return;
                    }
        
                    $session.searchResults = results;
                    log("//// RESULTS1 " + toPrettyString($session.searchResults));
        
                    var result = AdressesOutput(results);
                    log("//// result " + toPrettyString(result));
        
                    if (Object.prototype.toString.call(result.addresses) === '[object Array]') {
                        $reactions.answer("В базе для данного ФИО указано несколько адресов подключения. Выберите, пожалуйста, нужный.\n\n");
                    } else {
                        $reactions.answer("В базе для данного ФИО указан адрес " + result.addresses + ". Данная информация актуальна?");
                    }
        
                    $reactions.buttons(result.buttons);
                });


                
        
    state:SearchByPhone
        
    state:SearchByName
    
    state:SearchByAddress
        
    state:SuccessfulAuthorization




