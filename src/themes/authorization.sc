theme: /Auth
    
    state:Authorization
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
        
        if:$session.callerid
            a:Подскажите, вы хотите авторизоваться по номеру номер из $request?
        # else:Для продолжения разговора мне нужно Вас авторизовать. Укажите, пожалуйста, номер телефона, привязанный к счёту.
            
            
