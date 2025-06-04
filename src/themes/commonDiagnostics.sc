theme: /CommonDiagnostics
    
    state: CommonDiagnostics
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            $session.userAuthorized = true;
    
            commonprob(requestid, dialogid)
                .then(function(results) {
                    log("//// Статус от сервера: " + JSON.stringify(results));
    
                    if (results && results.status === "working") {
                        $reactions.answer("WORKING");
                        $reactions.transition("/CommonDiagnostics/CommonDiagnostics/PersonalDiagnostics");
                    } else if (results && results.status === "malfunction") {
                        $reactions.answer("MALFUNCTION");
                        $reactions.transition("/CommonDiagnostics/NegativeBalance");
                    } else {
                        $reactions.answer("Не удалось определить статус. Повторите позже.");
                        $reactions.transition("/CommonDiagnostics/UnknownError");
                    }
                })
                .catch(function(error) {
                    log("//// Глобальная ошибка: " + JSON.stringify(error));
                    $reactions.answer("Произошла ошибка. Проверьте логи.");
                });
                
        state: Error
            
                script:
                    $session.stateCounterInARow = $session.stateCounterInARow || 0;
                    $session.stateCounterInARow += 1;
                    
                if: $session.stateCounterInARow < 2
                   go!: /CommonDiag/CommonDiagnostics
                    
                else:
                    
                    a: go!: /anyError
                    
    state: AccessCheck
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            var callerid = $context.request.callerId;
            var account = $session.account || null;
    
            var reason = "Не работает интернет";
            $session.reason = reason;
    
            if ($session.userAuthorized === true) {
                getCustomerInfo(requestid, dialogid, callerid, account)
                    .then(function(customer) {
                        if (!customer) {
                            $reactions.answer("Ошибка получения данных. Повторите позже.");
                            $reactions.transition("/CommonDiagnostics/Error");
                            return;
                        }
    
                        log("Получен customer: " + JSON.stringify(customer));
    
                        if (customer.debt > 0) {
                            $reactions.answer("У вас есть задолженность.");
                            $reactions.transition("/Balance");
                        } else {
                            $reactions.answer("Проверка пройдена. Продолжаем диагностику.");
                            $reactions.transition("/PersonalDiagnostics");
                        }
                    })
                    .catch(function(error) {
                        log("Ошибка при вызове commonprob: " + JSON.stringify(error));
                        $reactions.answer("Произошла ошибка. Повторите позже.");
                        $reactions.transition("/CommonDiagnostics/CommonDiagnostic/Error");
                    });
            } else {
                $reactions.answer("Пожалуйста, авторизуйтесь, чтобы продолжить.");
                $reactions.transition("/Auth/Authorization");
            }
                        
    state: PersonalDiagnostics
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            var account = $session.account || null;
    
            probeEquipment(requestid, dialogid, account)
                .then(function(result) {
                    if (!result) {
                        $reactions.answer("Ошибка при диагностике оборудования.");
                        $reactions.transition("/CommonDiagnostics/CommonDiagnostic/Error");
                        return;
                    }
    
                    log("Результат диагностики оборудования: " + JSON.stringify(result));
    
                    if (result.status === "malfunction") {
                        $reactions.answer("Обнаружена неисправность. Переход к специалисту.");
                        $reactions.transition("/SwitchIsNotResponding");
                    } else {
                        $reactions.answer("Оборудование работает, соединяю с технической поддержкой.");
                        $reactions.transition("/СonnectionTechSupport");
                    }
                })
                .catch(function(error) {
                    log("Ошибка при вызове probe/equipment: " + JSON.stringify(error));
                    $reactions.answer("Произошла ошибка при диагностике оборудования.");
                    $reactions.transition("/CommonDiagnostics/CommonDiagnostic/Error");
                });
                
    state: TicketIsAlreadyCreated
        
        script:
            var reason = $session.reason || "неизвестна";
            var recoveryHours = $session.recovery_hours;
        
            $reactions.say("Нам уже известно о проблеме, и наши специалисты работают над её решением. Причина сбоя: " + reason + ".");
        
            if (recoveryHours) {
                $reactions.say("Примерное время устранения неполадок: " + recoveryHours + " ч.");
            } else {
                $reactions.answer("К сожалению, не могу сообщить точные сроки устранения неполадок.");
            }
            $reactions.transition("/SomethingElse");
    
    state: SwitchIsNotResponding
        a: Уточните, пожалуйста, есть ли перебои с электричеством по Вашему адресу?
        state: PowerWasLost
            q: $agree
            script:
                $analytics.setScenarioAction("Перебои с элктричеством - Да")
                $reactions.transition("/CommonDiagnostics/EqipmentCheck");
        state: PowerWasStablr
            q: $disagree
            script:
                $analytics.setScenarioAction("Перебои с элктричеством - Нет")
                $reactions.transition("/CommonDiagnostics/EqipmentCheck");
                
    state: EquipmentCheck
        a: Подскажите, вы сейчас находитесь рядом с оборудованием?
        state: EquipmentCheckYes
            q: $agree
            go!: /GeneralStates/ConnectionTechSupport
        state: LocationNotHome
            q: $disagree
            script:
                $session.notReadyForProbe = true || $session.notReadyForProbe
                $reactions.answer(" Для дальнейшего решения проблемы необходимо находиться рядом с оборудованием. Когда будете готовы к диагностике, напишите 'Готов к диагностике'.");
                $reactions.transition("/SomethingElse"); 