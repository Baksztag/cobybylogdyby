/**
 * Created by Admin on 2017-02-18.
 */
var socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/game/");
var username = "";

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
    }
};

socket.onclose = function () {

};

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

function initUI() {
    show(id("loginContainer"))
    hide(id("roomsContainer"));
    hide(id("gameLobbyContainer"));
    hide(id("gameContainer"));
    hide(id("resultsContainer"));

    hide(id("startGame"));
    hide(id("newQuestion"));
    hide(id("addQuestion"));
};

function toggle(container) {
    if (id(container + "Container").getAttribute("disabled") === "true") {
        show(id(container + "Container"));
    } else {
        hide(id(container + "Container"));
    }
};

function newUser(username) {
    if (username != "") {
        var obj = new Object();
        obj.action = "newUser";
        obj.username = username;
        socket.send(JSON.stringify(obj));
    }
}

function newUserResult(data) {
    if (data.result === "success") {
        username = data.username;
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
        var obj = new Object();
        obj.action = "newRoom";
        obj.roomName = name;
        obj.username = username;
        socket.send(JSON.stringify(obj));
        id("roomName").innerHTML = "Pokoj: " + name;
    }
};

function newRoomResult(data) {
    if (data.result === "success") {
        toggle("rooms");
        toggle("gameLobby");
        id("questionList").innerHTML = "";
        id("startGame").disabled = false;
        id("startGame").style.visibility = "visible";
        id("newQuestion").disabled = false;
        id("newQuestion").style.visibility = "visible";
        id("addQuestion").disabled = false;
        id("addQuestion").style.visibility = "visible";

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
        id("roomList").insertAdjacentHTML("afterbegin", "<button id='room-" + data.rooms[i] + "' class='btn btn-info'>" + data.rooms[i] + "</button>");
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
    obj.username = username;
    socket.send(JSON.stringify(obj));
}

function joinRoomResult(data) {
    toggle("rooms");
    toggle("gameLobby");
    id("roomName").innerHTML = "Pokoj: " + data.roomName;
    hide(id("startGame"));
    hide(id("newQuestion"));
    hide(id("addQuestion"));
}

function leaveRoom() {
    var obj = {};
    obj.action = "leave";
    obj.username = username;
    socket.send(JSON.stringify(obj));
}

function leaveRoomResult(data) {
    toggle("gameLobby");
    toggle("rooms");
    hide(id("startGame"));
    hide(id("newQuestion"));
    hide(id("addQuestion"));
}

function updateUserList(data) {
    id("playerList").innerHTML = "";
    data.users.forEach(function (user) {
        id("playerList").insertAdjacentHTML("beforeEnd", "<li>" + user + "</li>");
    });
}

function addQuestion(question) {
    if(question != "") {
        var obj = {};
        obj.action = "newQuestion";
        obj.question = question;
        obj.roomName = id("roomName").innerHTML.slice(7);
        socket.send(JSON.stringify(obj));
    }
    id("newQuestion").value = "";
}

function updateQuestionList(data) {
    id("questionList").innerHTML = "";
    data.questionList.forEach(function (question) {
        id("questionList").insertAdjacentHTML("beforeEnd", "<li>" + question + "</li>");
    });
};