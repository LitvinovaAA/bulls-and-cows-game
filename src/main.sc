require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: common.js
    module = sys.zb-common

require: functions.js
    
patterns:
    $number = $regex<\d+>

theme: /

    state: Start
        q!: $regex</start>
        a: Привет, я чат-бот, с которым можно сыграть в игру "Быки и коровы". 
        a: Что Вы хотите сделать?
        buttons:
            "Правила" -> /GameRules
            "Начать новую игру" -> /StartGame
        
        state: LocalCatchAll
            event: noMatch
            a: Я Вас не понял.
            go!: /Menu
    
    state: GameRules
        intent!: /Rules
        a: Я задумываю четырехзначное число с неповторяющимися цифрами, Ваша задача – отгадать это число, количество попыток неограниченно. Каждый Ваш ход – это четырехзначное число с неповторяющимися цифрами, в ответ я скажу, сколько цифр угадано без совпадения с их позициями в тайном числе (то есть количество коров) и сколько угадано вплоть до позиции в тайном числе (то есть количество быков).
        a: Начнем?
        buttons:
            "Да" -> /GameRules/Yes
            "Нет" -> /GameRules/No
        
        state: Yes
            intent: /Yes
            a: Удачи!
            go!: /StartGame
            
        state: No
            intent: /No
            a: Если передумайте – напишите "давай играть"
            
    state: StartGame
        intent!: /LetsPlay
        scriptEs6:
           $session.targetNumber = generateTargetNumber();

        random:
            a: Число загадано. Попробуйте угадать! 
            a: Я загадал число, Ваш ход!
            a: Я загадал число, дело за Вами!
        
        state: UserInput
            q: * $number *
            script:
                $session.userNumber = $parseTree._number.toString()
            go!: /ValidateUserInput
            
        state: LocalCatchAll
            event: noMatch
            a: Вы должны ввести четырехзначное число, используя цифры.
    
    state: ValidateUserInput
        scriptEs6:
            if ($session.userNumber.length !== 4) {
                $reactions.answer("Число должно быть четырехзначным!\nВведите другое число.");
                $reactions.transition( {value: "/StartGame/UserInput", deferred: true} )
            } else if (new Set($session.userNumber).size !== 4) {
                $reactions.answer("Число должно состоять из неповторяющихся цифр!\nВведите другое число.");
                $reactions.transition( {value: "/StartGame/UserInput", deferred: true} )
            } else if ($session.userNumber[0] === '0') {
                $reactions.answer("Число не может начинаться с нуля!\nВведите другое число.");
                $reactions.transition( {value: "/StartGame/UserInput", deferred: true} )
            } else {
                $reactions.transition({value: "/CompareNumbers", deferred: false});
            }
        
    state: CompareNumbers
        script:
            $session.samePosition = compareNumbers($session.targetNumber,$session.userNumber)[0]
            $session.commonDigits = compareNumbers($session.targetNumber,$session.userNumber)[1]
        go!: /Results
        
    state: Results
        script:
            if ($session.samePosition !== 4) {
                $reactions.answer("Быков: " + $session.samePosition + ", коров: " + $session.commonDigits + ".");
                $reactions.transition({value: "/StartGame/UserInput", deferred: true});
            }else {
                $reactions.answer("Поздравляю! Вы угадали загаданное число.");
                $reactions.transition({value: "/Menu", deferred: false});
            }

    state: Menu
        intent!: /Help
        a: Что Вы хотите сделать?
        buttons:
            "Правила" -> /GameRules
            "Начать новую игру" -> /StartGame
            "Завершить диалог" -> /Bye
       
    state: Hello
        intent!: /Hello
        a: Привет!
        go!: /Menu

    state: Bye
        intent!: /Bye
        script:
            $jsapi.stopSession();
        a: До свидания, возвращайтесь еще!
        
    state: NoMatch || noContext = true
        event!: noMatch
        a: Я Вас не понял.
        go!: /Menu