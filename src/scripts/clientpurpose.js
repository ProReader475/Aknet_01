function handleQuery(query) {
    if (query === '"Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]" Временный доступ') {
        $reactions.transition("/Identification/AskPhone");
        $session.purpose = "$GetCredit";
    } else if (query === '"Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]" Технические неполадки') {
        $reactions.transition("/Identification/AskPhone");
        $session.purpose = "$NoInternetOrTV";
    } else if (query === '"Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]" Узнать баланс ЛС') {
        $reactions.transition("/Identification/AskPhone");
        $session.purpose = "$CheckBalance";
    } else if (query === '"Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]" Оплата услуг') {
        $reactions.transition("/CheckBalance/PaymentMethods");
        $session.purpose = "$PayServices";
    } else if (query === '"Подскажите, чем я могу Вам помочь? | [Временный доступ] | [Технические неполадки] | [Подключение новых услуг] | [Оплата услуг] | [Узнать баланс ЛС]" Подключение новых услуг') {
        $reactions.transition("/AddService/WantToConnect");
        $session.purpose = "$ConnectInternetOrTV";
    } else if ($session.patternMatched === "$GetCredit" || $session.patternMatched === "$First") {
        $reactions.transition("/Identification/AskPhone");
        $session.purpose = "$GetCredit";
    } else if ($session.patternMatched === "$NoInternetOrTV" || $session.patternMatched === "$Second") {
        $reactions.transition("/Identification/AskPhone");
        $session.purpose = "$NoInternetOrTV";
    } else if ($session.patternMatched === "$CheckBalance" || $session.patternMatched === "$Fifth") {
        $reactions.transition("/Identification/AskPhone");
        $session.purpose = "$CheckBalance";
    } else if ($session.patternMatched === "$PayServices" || $session.patternMatched === "$Fourth") {
        $reactions.transition("/CheckBalance/PaymentMethods");
        $session.purpose = "$PayServices";
    } else if ($session.patternMatched === "$ConnectInternetOrTV" || $session.patternMatched === "$Third") {
        $reactions.transition("/AddService/WantToConnect");
        $session.purpose = "$ConnectInternetOrTV";
    }
}
