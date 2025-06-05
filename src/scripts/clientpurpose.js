function handleQuery($session) {
    log("///// $session.patternMatched " + toPrettyString($session.patternMatched))
    if ($session.patternMatched === "$GetCredit" || $session.patternMatched === "$First") {
        $reactions.transition("/Auth/Authorization");
        $session.purpose = "$GetCredit";
    } else if ($session.patternMatched === "$NoInternetOrTV" || $session.patternMatched === "$Second") {
        $reactions.transition("/Auth/Authorization");
        $session.purpose = "$NoInternetOrTV";
    } else if ($session.patternMatched === "$CheckBalance" || $session.patternMatched === "$Fifth") {
        $reactions.transition("/Auth/Authorization");
        $session.purpose = "$CheckBalance";
    } else if ($session.patternMatched === "$PayServices" || $session.patternMatched === "$Fourth") {
        $reactions.transition("/CheckBalance/PaymentMethods");
        $session.purpose = "$PayServices";
    } else if ($session.patternMatched === "$ConnectInternetOrTV" || $session.patternMatched === "$Third") {
        $reactions.transition("/AddService/WantToConnect");
        $session.purpose = "$ConnectInternetOrTV";
    }
}

export default { 
    handleQuery
    
};
