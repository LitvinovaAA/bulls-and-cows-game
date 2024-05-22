function generateTargetNumber() {
    var k = 1;
    var number = '';
                
    while (k <= 4) {
                
        if (k === 1) {
            var digit = Math.floor(Math.random() * 9) + 1;
            number += digit.toString();
            k++;
        } else {
            var digit = Math.floor(Math.random() * 10);
            
            if (number.includes(digit.toString())) {
                continue;
            } else {
                number += digit.toString();
                k += 1;
            }
        }
    }

    return number;
}

function compareNumbers(targetNumber, userNumber) {
    var samePosition = 0
    var commonDigits = 0
            
    for (var i = 0; i < 4; i++) {
        if (targetNumber[i]  === userNumber[i]) {
            samePosition++;
        }
                
        for (var j = 0; j < 4; j++) {
            if (targetNumber[i] === userNumber[j]) {
                commonDigits++;
                break;
            }
        }
    }

    commonDigits = commonDigits - samePosition;

    return [samePosition, commonDigits]
}