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

initUI = function () {
    show(id("loginContainer"))
    hide(id("roomsContainer"));
    hide(id("gameLobbyContainer"));
    hide(id("gameContainer"));
    hide(id("resultsContainer"));
};

toggle = function (container) {
    if(id(container + "Container").getAttribute("disabled") === "true") {
        show(id(container + "Container"));
    } else {
        hide(id(container + "Container"));
    }
};

function newUser(username) {
    if(username != "") {
        var obj = new Object();
        obj.action = "newUser";
        obj.username = username;
        socket.send(JSON.stringify(obj));
    }
}

function newUserResult(data) {
    if(data.result === "success") {
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
    if(name != "") {
        var obj = new Object();
        obj.action = "newRoom";
        obj.roomName = name;
        socket.send(JSON.stringify(obj));
        id("roomName").innerHTML = name;
        id("playerList").innerHTML = "sdfgs";
        id("questionList").innerHTML = "asdfa";
    }
};

function newRoomResult(data) {
    console.log(data.result);
    if(data.result === "success") {
        toggle("rooms");
        toggle("gameLobby");
    }
    else {
        id("newRoomError").innerHTML = "Nazwa pokoju " + data.roomName + " jest zajeta.";
        id("newRoomName").value = "";
    }
}