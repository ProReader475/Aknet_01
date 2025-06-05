theme: /Status
    
    state: ApplicationStatus
        q!: $CheckApplicationStatus
        script:
            var requestid = $context.request.questionId ||  "c62f2e27-d5a7-4bbb-a0c6-bb1640ca82db";
            var dialogid = $context.sessionId || "2f822a81-200b-50d7-29c2-94317f44d86f.b1cd43e6-b3e3-4b35-97b3-4a298b4ee141";
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
                $reactions.transition("/Auth/Authorization");
            }