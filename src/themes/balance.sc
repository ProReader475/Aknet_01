theme: /CheckBalance
    
    state: Balance
        q!: $CheckBalance
        script:
            $session.reason = "Узнать баланс"
    
        if: $session.userAuthorized == true
            script:
                fetchBalanceCustomerData(
                    $session.requestid,
                    $session.dialogid,
                    $session.callerid,
                    $session.account
                ).then(function(customer) {
                    if (!customer) {
                        $reactions.answer("Не удалось получить информацию. Попробуйте позже.");
                        $reactions.transition("/SomethingElse");
                        return;
                    }
    
                    $session.customer = customer;
    
                    var debt = parseFloat(customer.debt || 0);
                    var balance = parseFloat(customer.balance || 0);
                    var account = customer.account || "неизвестно";
                    var name = customer.name || "неизвестно";
                    var address = customer.address || "неизвестно";
    
                    $reactions.answer("Номер Вашего лицевого счёта: " + account);
                    $reactions.answer("ФИО: " + name);
                    $reactions.answer("Адрес: " + address);
    
                    if (debt > 0) {
                        $reactions.answer("На Вашем счете есть задолженность в размере " + declineSom(debt) + ". Пополните его для восстановления доступа к услугам. Рассказать о способах оплаты?");
    
                        if ($session.transport === "telegram" || $session.transport === "widget" || $session.transport === "insta_official") {
                            $reactions.buttons(["Да", "Нет"]);
                        }
                    } else {
                        $reactions.answer("Ваш баланс: " + declineSom(balance) + ".");
                        $reactions.transition("/SomethingElse");
                    }
                });
        else:
            go!: /Identification/AskPhone
    
        state: Yes
            q: $regexp_i<(да|конечно|расскажи|да, расскажи|да, расскажите|давай|хочу узнать|интересно|поделись|да, хотелось бы узнать|да, пожалуйста|да, расскажи подробнее|да, расскажи о вариантах)>
            go!: /CheckBalance/PaymentMethods
    
        state: No
            q: $regexp_i<(нет|не надо|неинтересно|не хочу|не нужно|не сейчас|позже|может в другой раз|спасибо, не надо|не хочу знать|не рассказывай|нет, спасибо)>
            go!: /SomethingElse
        
        state: No
            q: q: $regexp_i<(выдайте\s+кредит\s+ещё\s+раз|дайте\s+временный\s+доступ\s+снова|предоставьте\s+доступ\s+повторно|снова\s+получить\s+доступ|повторно\s+выдать\s+кредит)>
            go!: /CheckYourDebt
    
    state: PaymentMethods
        a: 
            Оплатить услуги можно следующим образом:
            💻 На нашем сайте: [aknet.kg/oplata_uslug](https://aknet.kg/oplata_uslug/)
    
            🏧 В терминалах:
            - [Optima Bank](https://www.optimabank.kg/ru/offices-kyrgyzstan.html)
            - [UNIPAY](https://unipay.kg/)
            - [KICB](https://kicb.net/branch-network/)
            - [UMAI](https://umai.kg/terminal-list)
            - [Дос-Кредобанк](https://www.dcb.kg/ru/terminals/terminals)
            - M&TC (г.Кант)
            - Кыртелсат
            - TeremPay
            - [Банк Компаньон](https://www.kompanion.kg/ru/retail/bank-guarantee/)
            - Керемет банк
            - Касса24
            - Пэй 24
            - Quickpay
            - MegaCom
            - O!
    
            🌐 Интернет-банкинг:
            - [Optima24](https://optima24.kg/)
            - [KKB](https://kkb.kg/page/140)
            - [i-bank KICB](https://ibank.kicb.net/)
            - Бакай банк
            - Дос-Кредобанк
            - Капитал Банк — комиссия 0 сом
            - Керемет банк — комиссия 5 сом
    
            💳 Электронные кошельки:
            - ЭЛСОМ
            - UMAI
            - ИЛБИРС
            - MegaPay
            - О!Деньги
            - Balance.kg — комиссия 0 сом
    
            🏦 В кассах:
            - [Optima Bank](https://www.optimabank.kg/ru/offices-kyrgyzstan.html)
            - [Дос-Кредобанк](https://www.dcb.kg/ru/terminals/departments)
            - Бакай банк
            - М&TC
    
            📮 В отделениях [ГП "Кыргыз почтасы"](https://www.aknet.kg/files/media/logo_pay/pochta.pdf)
    
            🏢 В офисах нашей компании.
    
        go!: /SomethingElse
