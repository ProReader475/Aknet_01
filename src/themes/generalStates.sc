theme: /GeneralStates
    
    state: GlobalCatchAll || noContext = true
        event!: noMatch
        if: $session.dataBaseUnavailable == true
            go!: /DBUnavailable
        script:
            $session.stateCountInARow = $session.stateCountInARow || 0
            $session.stateCountInARow += 1
        if: $session.stateCountInARow < 2
            random:
                a: Извините, не понял вас. Переформулируйте, пожалуйста.
                a: Простите, не распознал ваш запрос. Попробуйте сформулировать другими словами, пожалуйста.
        else:
            a: Простите, так и не смог понять, что вы имели в виду.
            go!: /GeneralStates/ConnectionTechSupport
    
    state: Gratitude
        q!: * $Thanks *
        random:  
            a: Пожалуйста, рад помочь!
            a: Обращайтесь!
            a: Не за что! Я всегда готов помочь.
            a: Был рад помочь!
        go!: /SomethingElse
        
    state: Obsene 
        q!: * $Obsene *
        random:
            a: Не хотел Вас расстраивать.  Давайте попробуем еще раз?
            a: Прошу прощения, если я вас чем-то расстроил. Возможно, я не совсем понял ваш вопрос. Уточните, пожалуйста, что вас интересует?
        go!: /PreviousState
        
    state: WhoAreYou
        q!: * $WhoAreYou *
        random:
            a: Вы общаетесь с ботом-помощником Акнет. Могу помочь получить временный доступ, подключиться к нашему интернету или телевидению, а также устранить технические неполадки.
            a: Я чат-бот Акнета. Я умею выдавать временный доступ, подключать к нашим услугам, а также выявлять причины неполадок с интернетом или телевидением.
            a: Я чат-бот Акнета. Я могу выдать временный доступ, определить причину сбоев в работе интернета или телевидения, а ещё - помочь подключить наши услуги. 
        go!: /PreviousState  
        
    state: ConnectionTechSupport
        intent: /ДоступДляДругого
        a: Перевожу Вас на оператора. Пожалуйста, ожидайте и не выходите из чата.
        script:
            
            $session.stateCountInARow = $session.stateCountInARow || 0;
            $session.stateCountInARow += 1;
        if: $session.stateCountInARow < 2
        script:
            var history = $jsapi.chatHistory();
            var context = parseChatHistory(history);
            endChat($session.requestid, $session.dialogid, $session.callerid, $session.need_callback,context)
                .then(function(results) {
                    log("///////////////////////////////////////////RAVLASFASO");
                    $response.replies = $response.replies || [];
                    $response.replies.push({
                        type: "switch",
                        firstMessage: $jsapi.chatHistory(),
                        appendCloseChatButton: false,
                        destination: "test_2",
                        lastMessage: "Ждем вас снова!"
                    });
                })
                .catch(function(error) {
                    $reactions.answer("Возникла техническая ошибка. Напишите нам, пожалуйста, чуть позже.");
                    $reactions.transition("/SomethingElse");
                });

    
        state: Return
            event!: livechatFinished
            a: Диалог с оператором завершён, на связи снова бот-помощник.
            go!: /SomethingElse
        
            
    state: FileEvent || noContext = true 
        event!: fileEvent 
        scriptEs6:
            const messages = {
                file: "Поймал этот файл, но еще не умею с ним работать.",
                audio: "Вы отправили мне аудио, но прослушать его я пока не могу.",
                image: "К сожалению, я пока не умею обрабатывать изображения.",
                video: "Получил ваше видео, но пока что не умею с ним работать."
            };
            if (Array.isArray($request.data.eventData)) {
                $request.data.eventData.forEach(function (file, index) { 
                    if (messages[file.type]) {
                        $reactions.answer(messages[file.type]);
                    }
                });
            } 
        a: Пожалуйста, пришлите свой ответ текстом.
        
    state: DontWantToTalk
        q!: * $Operator *
        script:
            $session.stateCountInARow = $session.stateCountInARow || 0
            $session.stateCountInARow += 1
        if: $session.stateCountInARow < 2
            a: Уточните, пожалуйста, какой у Вас вопрос? Я постараюсь вам помочь или переведу Вас на оператора для продолжения консультации.
        else:
            go!: /GeneralStates/ConnectionTechSupport
            
        state: DontWantToTalkCatchAll || noContext = true
            event: noMatch
            a: Кажется, этот вопрос не в моей компетенции.
            go!: /GeneralStates/ConnectionTechSupport
            
    state: ReadyForProbe
            script: $session.reason = "Готов к диагностике"
            if: $session.userAuthorized == true
                if: $session.lastState !== "/SuccessfulAuthorization"
                    a: Подскажите, {{$session.searchResults[0].account}} - это Ваш лицевой счёт?
                else: 
                    script: $session.notReadyForProbe = false
                    a: Отлично! Теперь можем приступить к диагностике Вашего оборудования.
                    go!: /GeneralStates/ConnectionTechSupport
            else: 
                go!: /Auth/Authorization
                
            state: MyAccount
                q: * $MyAccount *
                script: $session.notReadyForProbe = false
                a: Отлично! Тогда можем приступить к диагностике вашего оборудования.
                go!: /GeneralStates/ConnectionTechSupport
                
            state: NotMyAccount
                q: * $NotMyAccount *
                go!: Auth/Authorization
                
            state: DontKnowAccount
                q: * $DontKnowAccount *
                go!: /GeneralStates/ConnectionTechSupport
                
    state: Recalculation
        intent: /ЗаявкаНаПерерасчет
        a: Понял Вас.
        go!: /GeneralStates/ConnectionTechSupport   
    
        
    