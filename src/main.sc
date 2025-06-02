require: requirements.sc

theme: /
    state: CheckDataBase
        q!: $regex</start>         
        q!: * $Hello *
        script:
            $jsapi.startSession();

            $session.transport = $request.rawRequest.transport            

            if($session.transport === "widget" || $session.transport === "external") {
                $session.phone = $request.rawRequest.client.client_phone
            } 
            
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            var callerid = $context.callerId || "неизвестен" ;
            
            checkDataBase(requestid, dialogid,callerid)
                .then(function(results) {
                    $reactions.transition("/Start");
                })
                .catch(function(error) {
                    $reactions.answer("Здравствуйте! Я бот-помощник. К сожалению, на данный момент не могу обработать Ваш запрос. Перевожу на оператора — ожидайте, пожалуйста.");
                    $reactions.transition("/GeneralStates/ConnectionTechSupport");
                });

    state: Start
        script: $session.transport = $request.rawRequest.transport
        random:
            a: Здравствуйте! Я - бот-помощник Акнет. Могу помочь получить временный доступ, подключиться к нашему интернету или кабельному телевидению, а также устранить неполадки при неработающем оборудовании.
            a: Приветствую! Я - чат-бот Акнет. Я умею выдавать временный доступ, подключать к нашим услугам, а также выявлять причины неполадок с интернетом или кабельным телевидением.
            a: Здравствуйте! Я - чат-бот Акнет. Смогу выдать временный доступ, определить причину сбоев при работе интернета или кабельного телевидения и даже помочь подключить наши услуги.
        go!: /Menu
        
    state: Menu
        buttons:
                "Получение кредита (временного доступа)" -> /Auth/Authorization
        if: $session.transport === "telegram" || $session.transport === "widget" || $session.transport === "insta_official"
            buttons:
                "Получение кредита (временного доступа)" -" -> /Auth/Authorization
                "Не работает интернет или телевидение" 
                "Подключение к интернету или телевидению" 
                "Оплатить услуги" 
                "Узнать баланс лицевого счеьта" 
        else:
            script:
                if($session.transport === "external") {
                    $reactions.answer("Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]");
                } else { 
                    $reactions.answer("1 - Получение кредита (временного доступа)\n2 - Не работает интернет или телевидение\n3 - Подключение к интернету или телевидению\n4 - Оплатить услуги\n5 - Узнать баланс лицевого счета");
                }
            
        state: CatchMenu
            q:$ConnectInternetOrTV
            q:$PayServices
            q:$GetCredit
            q:$NoInternetOrTV
            q:$CheckBalance 
            q:$First
            q:$Second
            q:$Third
            q:$Fourth
            q:$Fifth
            
            scriptEs6:
                let query = ($request.query);
                query = query.replace(/\n/g, " ").trim();

                log("_____________TEST__________" + query);
                
                handleQuery(query);

                log("______________$session__________________" + toPrettyString($session))
             
    state: SomethingElse
            random:
                a: Подскажите, у вас остались вопросы?
                a: Могу ли я помочь чем-то еще?
                a: Нужны ответы на ещё какие-либо вопросы?
                buttons:
                    "Получение кредита (временного доступа)"
                    "Не работает интернет или телевидение"
                    "Подключение к интернету или телевидению"
                    "Оплатить услуги"
                    "Узнать баланс лицевого счета"
                    
            state: NoQuestions
                q: $NoMoreQuestions
                go!: /Bye
                    
            state: CatchMenu
                q:$ConnectInternetOrTV
                q:$PayServices
                q:$GetCredit
                q:$NoInternetOrTV
                q:$CheckBalance 
                q:$First
                q:$Second
                q:$Third
                q:$Fourth
                q:$Fifth
                
                scriptEs6:
                    let query = ($request.query);
                    query = query.replace(/\n/g, " ").trim();
                    handleQuery(query);
    state: Bye
        q!: * $Bye *
        random:
            a: Если у Вас появятся вопросы, буду рад пообщаться снова! До свидания!
            a: Спасибо за обращение. Всего доброго!
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            var callerid = $context.callerId || "неизвестен" ;
            var need_callback = $context.need_callback || false;
            var history = $jsapi.chatHistory();
            var context = parseChatHistory(history);
            endChat(requestid, dialogid,callerid,need_callback,context)
                .then(function(results) {
                    $reactions.answer("Успешно записал");
                    $jsapi.stopSession();
                    
                })
                .catch(function(error) {
                    $reactions.answer("НЕУУспешно записал");
                    $jsapi.stopSession();
                });
            