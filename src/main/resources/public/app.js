/**
 * Created by Admin on 2017-02-18.
 */
var socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/game/");
var app = {
    username: "",
    roomName: ""
};

var lastMessageSent = new Date().getTime();

socket.onopen = function () {
    initUI();
};

socket.onmessage = function (msg) {
    var data = JSON.parse(msg.data);

    switch (data.action) {
        case "newUser":
            newUserResult(data);
            break;
        case "newRoom":
            newRoomResult(data);
            break;
        case "roomList":
            updateRoomList(data);
            break;
        case "join":
            joinRoomResult(data);
            break;
        case "leave":
            leaveRoomResult(data);
            break;
        case "listUsers":
            updateUserList(data);
            break;
        case "questionList":
            updateQuestionList(data);
            break;
        case "startGame":
            startGameResult(data);
            break;
        case "acceptQuestion":
            acceptQuestionResult(data);
            break;
        case "acceptAnswer":
            acceptAnswerResult(data);
            break;
        case "endGame":
            endGame(data);
            break;
        case "listResults":
            listResults(data);
            break;
        case "listAllResults":
            listAllResults(data);
            break;
        case "ping":
            pong();
            break;
    }
};

// socket.onclose = closeConnection();

//EVENT HANDLERS
id("addRoom").addEventListener("click", function () {
    addRoom(id("newRoomName").value);
    id("newRoomName").value = "";
});

id("newUser").addEventListener("click", function () {
    newUser(id("username").value);
});

id("leaveRoom").addEventListener("click", function () {
    leaveRoom();
});

id("addQuestion").addEventListener("click", function () {
    addQuestion(id("newQuestion").value);
});

id("startGame").addEventListener("click", function () {
    startGame();
});

id("acceptQuestion").addEventListener("click", function () {
    acceptQuestion(id("nextQuestion").value);
});

id("acceptAnswer").addEventListener("click", function () {
    acceptAnswer(id("newAnswer").value);
});

id("leaveGame").addEventListener("click", function () {
    leaveGame();
});

//HELPER FUNCTIONS
function id(id) {
    return document.getElementById(id);
}

function show(element) {
    element.style.visibility = "visible";
    element.setAttribute("disabled", "false");
}

function hide(element) {
    element.style.visibility = "hidden";
    element.setAttribute("disabled", "true");
}

function showGameLobbyElements() {
    id("startGame").disabled = false;
    id("startGame").style.visibility = "visible";
    id("newQuestion").disabled = false;
    id("newQuestion").style.visibility = "visible";
    id("addQuestion").disabled = false;
    id("addQuestion").style.visibility = "visible";
}

function hideGameLobbyElements() {
    id("startGame").disabled = true;
    id("startGame").style.visibility = "hidden";
    id("newQuestion").disabled = true;
    id("newQuestion").style.visibility = "hidden";
    id("addQuestion").disabled = true;
    id("addQuestion").style.visibility = "hidden";
}

function initUI() {
    show(id("loginContainer"));
    hide(id("roomsContainer"));
    hide(id("gameLobbyContainer"));
    hide(id("gameContainer"));
    hide(id("resultsContainer"));

    hide(id("startGame"));
    hide(id("newQuestion"));
    hide(id("addQuestion"));
}

function toggle(container) {
    if (id(container + "Container").getAttribute("disabled") === "true") {
        show(id(container + "Container"));
    } else {
        hide(id(container + "Container"));
    }
}

function newUser(username) {
    if (username != "") {
        var obj = {};
        obj.action = "newUser";
        obj.username = username;
        socket.send(JSON.stringify(obj));
    }
}

function newUserResult(data) {
    if (data.result === "success") {
        app.username = data.username;
        app.roomName = "lobby";
        toggle("login");
        toggle("rooms");
    }
    else {
        id("loginError").innerHTML = "Nick " + data.username + " jest zajety.";
        id("username").value = "";
    }
}

function addRoom(name) {
    if (name != "") {
        var obj = {};
        obj.action = "newRoom";
        obj.roomName = name;
        obj.username = app.username;
        socket.send(JSON.stringify(obj));
        id("roomName").innerHTML = "Pokoj: " + name;
    }
}

function newRoomResult(data) {
    if (data.result === "success") {
        app.roomName = data.roomName;
        toggle("rooms");
        toggle("gameLobby");
        id("questionList").innerHTML = "";
        showGameLobbyElements();

        // show(id("startGame"));
        // show(id("newQuestion"));
        // show(id("addQuestion"));
    }
    else {
        id("newRoomError").innerHTML = "Nazwa pokoju " + data.roomName + " jest juz zajeta.";
        id("newRoomName").value = "";
    }
}

function updateRoomList(data) {
    id("roomList").innerHTML = "";
    for (var i = 0; i < data.rooms.length; i++) {
        id("roomList").insertAdjacentHTML("beforeEnd", "<li><button id='room-" + data.rooms[i] + "' class='btn btn-info'>" + data.rooms[i] + "</button></li>");
    }

    var rooms = id("roomList").getElementsByTagName("button");
    for (i = 0; i < rooms.length; i++) {
        rooms[i].addEventListener("click", function () {
            var room = this.id.slice(5);
            joinRoom(room);
        })
    }
}

function joinRoom(room) {
    var obj = {};
    obj.action = "join";
    obj.roomName = room;
    obj.username = app.username;
    socket.send(JSON.stringify(obj));
}

function joinRoomResult(data) {
    app.roomName = data.roomName;
    toggle("rooms");
    toggle("gameLobby");
    id("roomName").innerHTML = "Pokoj: " + data.roomName;
    hideGameLobbyElements();
}

function leaveRoom() {
    var obj = {};
    obj.action = "leave";
    obj.username = app.username;
    obj.roomName = app.roomName;
    socket.send(JSON.stringify(obj));
}

function leaveRoomResult(data) {
    app.roomName = "lobby";
    toggle("gameLobby");
    toggle("rooms");
    hideGameLobbyElements()
}

function updateUserList(data) {
    id("playerList").innerHTML = "";
    data.users.forEach(function (user) {
        id("playerList").insertAdjacentHTML("afterBegin", "<li>" + user + "</li>");
    });
}

function addQuestion(question) {
    if (question != "") {
        var obj = {};
        obj.action = "newQuestion";
        obj.question = question;
        obj.username = app.username;
        obj.roomName = app.roomName;
        socket.send(JSON.stringify(obj));
    }
    id("newQuestion").value = "";
}

function updateQuestionList(data) {
    id("questionList").innerHTML = "";
    data.questionList.forEach(function (question) {
        id("questionList").insertAdjacentHTML("beforeEnd", "<li>" + question + "</li>");
    });
}

function startGame() {
    var o = {};
    o.action = "startGame";
    o.username = app.username;
    o.roomName = app.roomName;
    socket.send(JSON.stringify(o));
}

function startGameResult(data) {
    if (data.result === "failure") {
        id("startGameError").innerHTML = data.details;
    }
    else {
        toggle("gameLobby");
        toggle("game");
        hideGameLobbyElements();
        hide(id("answer"));
        show(id("question"));
        id("questionInfo").innerHTML = data.question;
    }
}

function acceptQuestion(question) {
    if (question != "") {
        var o = {};
        o.action = "acceptQuestion";
        o.question = question;
        o.username = app.username;
        o.roomName = app.roomName;
        socket.send(JSON.stringify(o));
        hide(id("question"));
        id("nextQuestion").value = "";
    }

}

function acceptQuestionResult(data) {
    if (data.result === "failure") {
        id("messages").innerHTML = data.message;
    }
    else {
        id("messages").innerHTML = "";
        show(id("answer"));
        id("previousQuestion").innerHTML = data.question;
    }
}

function acceptAnswer(answer) {
    if (answer != "") {
        var o = {};
        o.action = "acceptAnswer";
        o.answer = answer;
        o.username = app.username;
        o.roomName = app.roomName;
        socket.send(JSON.stringify(o));
        hide(id("answer"));
        id("newAnswer").value = "";
    }
}

function acceptAnswerResult(data) {
    if (data.result === "failure") {
        id("messages").innerHTML = data.message;
    }
    else {
        id("messages").innerHTML = "";
        show(id("question"));
        id("questionInfo").innerHTML = data.question;
    }
}

function endGame() {
    app.roomName = "lobby";
    toggle("game");
    toggle("results");
}

function listResults(data) {
    id("resultList").innerHTML = "";
    for (var i = 0; i < data.result.length; i++) {
        id("resultList").insertAdjacentHTML("beforeEnd",
            "<li>" +
            "<span class='questionPosition'>" +
            data.result[i].question +
            "</span>" +
            "<span class='answerPosition'>" +
            data.result[i].answer +
            "</span>" +
            "</li>");
    }
}

function leaveGame() {
    toggle("results");
    toggle("rooms");
    id("resultList").innerHTML = "";
    id("allResultsList").innerHTML = "";
}

function listAllResults(data) {
    id("allResultsList").innerHTML = "";
    for (var i = 0; i < data.result.length; i++) {
        id("allResultsList").insertAdjacentHTML("beforeEnd",
            "<li>" +
            "<span class='questionPosition'>" +
            data.result[i].question +
            "</span>" +
            "<span class='answerPosition'>" +
            data.result[i].answer +
            "</span>" +
            "</li>");
    }
}

// function ping() {
//     var o = {};
//     o.action = "ping";
//     socket.send(JSON.stringify(o));
// }

// function closeConnection() {
//     var o = {};
//     o.action = "close";
//     o.username = username;
//     socket.send(JSON.stringify(o));
// }

// function abortGame() {
//     toggle("game");
//     toggle("rooms");
//     id("resultList").innerHTML = "";
//     id("allResultsList").innerHTML = "";
// }

// setInterval(
//     function () {
//         var interval = 30 * 1000;
//         var currentTime = new Date().getTime();
//         if (lastMessageSent < currentTime - interval) {
//             ping();
//             lastMessageSent = currentTime;
//         }
//     },
//     1000
// );

function pong() {
    var o = {};
    o.action = "pong";
    o.username = app.username;
    o.roomName = app.roomName;
    socket.send(JSON.stringify(o));
}