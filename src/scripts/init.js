bind("selectNLUResult", function($context) {
    if ($context.nluResults.patterns.length > 0) {
        $context.nluResults.selected = $context.nluResults.patterns[0];
        $context.session.patternMatched = $context.nluResults.selected.pattern;
    }
});

bind("postProcess", function($context){
    $context.session.previousLastState = $context.session.lastState;
    $context.session.lastState = $context.currentState;
})