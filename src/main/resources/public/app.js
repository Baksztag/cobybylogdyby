/**
 * Created by Admin on 2017-02-18.
 */
var socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/game/");

socket.onopen = function () {
    initUI();
};

socket.onmessage = function () {

};

socket.onclose = function () {

};

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