theme: /CommonDiagnostics
    
    state: CommonDiagnostic
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            $session.userAuthorized = true;
    
            commonprob(requestid, dialogid)
                .then(function(results) {
                    log("//// Статус от сервера: " + JSON.stringify(results));
    
                    if (results && results.status === "working") {
                        $reactions.transition("/CommonDiagnostics/PersonalDiagnostics");
                    } else if (results && results.status === "malfunction") {
                        $reactions.answer("MALFUNCTION");
                        $reactions.transition("/CommonDiagnostic/NegativeBalance");
                    } else {
                        $reactions.answer("Не удалось определить статус. Повторите позже.");
                        $reactions.transition("/CommonDiagnostic/UnknownError");
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
                  go!: /CommonDiagnostics/CommonDiagnostic
                    
                else:
                    
                    a: go!: /anyError
                    
    state: AccessCheck
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            var callerid = $context.request.callerId;
            var account = $session.account || null;
            $session.userAuthorized = true;
            var reason = "Не работает интернет";
            $session.reason = reason;
    
            if ($session.userAuthorized === true) {
                getCustomerInfo(requestid, dialogid, callerid, account)
                    .then(function(customer) {
                        if (!customer) {
                            $reactions.answer("Ошибка получения данных. Повторите позже.");
                            $reactions.transition("/CommonDiagnostics/CommonDiagnostic/Error");
                            return;
                        }
    
                        log("Получен customer: " + JSON.stringify(customer));
    
                        if (customer.debt > 0) {
                            $reactions.answer("У вас есть задолженность.");
                            $reactions.transition("/Balance");
                        } else {
                            $reactions.answer("Проверка пройдена. Продолжаем диагностику.");
                            $reactions.transition("/CommonDiagnostics/PersonalDiagnostics");
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
            var callerid = $context.request.callerId;
            var account = $session.account || null;
            $session.userAuthorized = true;
            var reason = "Не работает интернет";
            $session.reason = reason;
    
            if ($session.userAuthorized === true) {
                getCustomerInfo(requestid, dialogid, callerid, account)
                    .then(function(customer) {
                        if (!customer) {
                            $reactions.answer("Ошибка получения данных. Повторите позже.");
                            $reactions.transition("/CommonDiagnostics/CommonDiagnostic/Error");
                            return;
                        }
    
                        log("Получен customer: " + JSON.stringify(customer));
    
                        if (customer.debt > 0) {
                            $reactions.answer("У вас есть задолженность.");
                            $reactions.transition("/Balance");
                            return;
                        }
    
                        // Если долга нет — продолжаем диагностику оборудования
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
    
                    })
                    .catch(function(error) {
                        log("Ошибка при вызове customer/get: " + JSON.stringify(error));
                        $reactions.answer("Произошла ошибка. Повторите позже.");
                        $reactions.transition("/CommonDiagnostics/CommonDiagnostic/Error");
                    });
    
            } else {
                $reactions.answer("Пожалуйста, авторизуйтесь, чтобы продолжить.");
                $reactions.transition("/Auth/Authorization");
            }
