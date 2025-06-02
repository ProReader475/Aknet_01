require: requirements.sc

theme: /
    state: EntryPoint
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
            
            commonProb(requestid, dialogid)
                .then(function(results) {
                    $reactions.transition("/Start");
                })
                .catch(function(error) {
                    $reactions.transition("/Start");
                });

    state: Start
        script: $session.transport = $request.rawRequest.transport
        random:
            a: Здравствуйте! Я - бот-помощник Акнет. Могу помочь получить временный доступ, подключиться к нашему интернету или кабельному телевидению, а также устранить неполадки при неработающем оборудовании.
            a: Приветствую! Я - чат-бот Акнет. Я умею выдавать временный доступ, подключать к нашим услугам, а также выявлять причины неполадок с интернетом или кабельным телевидением.
            a: Здравствуйте! Я - чат-бот Акнет. Смогу выдать временный доступ, определить причину сбоев при работе интернета или кабельного телевидения и даже помочь подключить наши услуги.
        go!: /Start/AfterState
        
        state: AfterState
            go!: /Menu 
    
    state: Menu
        if: $session.transport === "telegram" || $session.transport === "widget" || $session.transport === "insta_official"
            buttons:
                "Получение кредита (временного доступа)" 
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
        if: $session.transport === "telegram" || $session.transport === "widget" || $session.transport === "insta_official"
            buttons:
                "Получение кредита (временного доступа)"
                "Не работает интернет или телевидение"
                "Подключение к интернету или телевидению"
                "Оплатить услуги"
                "Узнать баланс лицевого счета"
        else:
            script:
                if($session.transport === "external") {
                    $reactions.answer("Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]");
                } else { 
                    $reactions.answer("1 - Получение кредита (временного доступа)\n2 - Не работает интернет или телевидение\n3 - Подключение к интернету или телевидению\n4 - Оплатить услуги\n5 - Узнать баланс лицевого счета");
                }
                
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
            $jsapi.stopSession();