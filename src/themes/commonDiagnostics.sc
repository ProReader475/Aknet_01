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
                        $reactions.transition("/CommonDiagnostics/Error");
                    });
            } else {
                $reactions.answer("Пожалуйста, авторизуйтесь, чтобы продолжить.");
                $reactions.transition("/Authorization");
            }
