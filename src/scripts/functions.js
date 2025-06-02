function parseChatHistory(rawHistory) {
    // rawHistory — строка с пометками "BOT", "CLIENT" и текстами сообщений
    
    var lines = rawHistory.split('\n');
    var result1 = [];
    var result = {};
    var currentAuthor = null;
    var currentTextLines = [];
    
    function saveMessage() {
        if (currentAuthor && currentTextLines.length > 0) {
            var text = currentTextLines.join('\n').trim();
            result1.push({ author: currentAuthor, text: text });
            currentTextLines = [];
        }
    }
    
    for (var i = 0; i < lines.length; i++) {
        var line = lines[i].trim();
        if (line === "BOT" || line === "CLIENT") {
            saveMessage();
            currentAuthor = line;
        } else if (line.length > 0) {
            currentTextLines.push(line);
        }
    }
    saveMessage();
    
    result.messages = result1;
    return result;
}
