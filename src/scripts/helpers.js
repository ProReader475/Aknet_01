function declineSom(number) {
    var n = Math.abs(number) % 100;
    var n1 = n % 10;
    
    if (n > 10 && n < 20) {
        return number + ' сомов';
    }
    if (n1 > 1 && n1 < 5) {
        return number + ' сома';
    }
    if (n1 === 1) {
        return number + ' сом';
    }
    return number + ' сомов';
}