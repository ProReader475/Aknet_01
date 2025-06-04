theme: /Status
    state: ApplicationStatus
        q!: $CheckApplicationStatus
        script:
            var requestid = $context.request.questionId;
            var dialogid = $context.sessionId;
            var account = $session.account || null;
    
            $session.reason = "Проверка статуса заявки";
    
            if ($session.userAuthorized === true) {
                getApplicationStatus(requestid, dialogid, account)
                    .then(function(applications) {
                        if (!applications || applications.length === 0) {
                            $reactions.answer("На данный момент у вас нет активных заявок.");
                        } else if (applications.length === 1) {
                            var app = applications[0];
                            $reactions.answer("У вас оформлена заявка: " + app.type_name + ", срок исполнения: " + app.execution_date + ".");
                        } else {
                            var response = "У вас есть " + applications.length + " заявки:\n";
                            applications.forEach(function(app) {
                                response += app.type_name + ", срок исполнения: " + app.execution_date + "\n";
                            });
                            $reactions.answer(response);
                        }
                        $reactions.transition("/SomethingElse");
                    })
                    .catch(function(error) {
                        log("Ошибка при получении статуса заявок: " + JSON.stringify(error));
                        $reactions.answer("Произошла ошибка при получении статуса заявок. Пожалуйста, попробуйте позже.");
                        $reactions.transition("/SomethingElse");
                    });
            } else {
                $reactions.answer("Пожалуйста, авторизуйтесь, чтобы продолжить.");
                $reactions.transition("/Authorization");
            }