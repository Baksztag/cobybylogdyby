/**
 * Created by Admin on 2017-02-18.
 */
var socket = new WebSocket("wss://" + location.hostname + ":" + location.port + "/game/");

socket.onopen = function () {
    initUI();
};

socket.onmessage = function () {

};

socket.onclose = function () {

};

//EVENT HANDLERS
id("addRoom").addEventListener("click", function () {
    addRoom(id("newRoomName").value);
    id("newRoomName").value = "";
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
    show(id("roomsContainer"));
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

function addRoom(name) {
    if(name != "") {
        var obj = new Object();
        obj.action = "newRoom";
        obj.roomName = name;
        socket.send(JSON.stringify(obj));
        toggle("rooms");
        toggle("gameLobby");
        id("roomName").innerHTML = name;
        id("playerList").innerHTML = "sdfgs";
        id("questionList").innerHTML = "asdfa";
    }
};