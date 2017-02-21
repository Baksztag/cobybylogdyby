import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
public class GameControls {
    private Lobby lobby;
    private List<GameRoom> rooms;


    public GameControls() {
        this.rooms = new LinkedList<>();
        this.lobby = new Lobby();
    }

    public void newUser(Session user, String username) throws JSONException, IOException {
        if (!usernameTaken(username)) {
            lobby.addUser(user, username);
            updateRoomList();
            user.getRemote().sendString(String.valueOf(
                    new JSONObject()
                            .put("action", "newUser")
                            .put("result", "success")
                            .put("username", username)
            ));
        } else {
            user.getRemote().sendString(String.valueOf(
                    new JSONObject()
                            .put("action", "newUser")
                            .put("result", "error")
                            .put("username", username)
            ));
        }
    }

    private boolean usernameTaken(String username) {
        boolean usernameInRooms = false;
        for (Room room : rooms) {
            if (room.usernameTaken(username))
                usernameInRooms = true;
        }
        return lobby.usernameTaken(username) || usernameInRooms;
    }

    public void addRoom(String name, Session host, String username) throws JSONException, IOException {
        if (!roomNameTaken(name)) {
            GameRoom newRoom = new GameRoom(name, host);
            rooms.add(newRoom);
            updateRoomList();
            lobby.removeUser(host);
            newRoom.addUser(host, username);
            notifyUser(host, new JSONObject()
                    .put("action", "newRoom")
                    .put("result", "success")
                    .put("roomName", name)
            );
            updateUserList(newRoom);
        } else {
            notifyUser(host, new JSONObject()
                    .put("action", "newRoom")
                    .put("result", "error")
                    .put("roomName", name)
            );
        }

    }

    private boolean roomNameTaken(String name) {
        for (Room room : rooms) {
            if (room.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void updateRoomList() throws JSONException, IOException {
        List<String> roomNames = new LinkedList<>();
        for (GameRoom room : rooms) {
            if(!room.gameInProgress()) {
                roomNames.add(room.getName());
            }
        }
        notifyAllUsers(new JSONObject()
                .put("action", "roomList")
                .put("rooms", roomNames));
    }

    public void joinRoom(Session user, String roomName, String username) throws JSONException, IOException {
        GameRoom roomToJoin = getRoomByName(roomName);
        if (roomToJoin != null) {
            lobby.removeUser(user);
            roomToJoin.addUser(user, username);
            notifyUser(user, new JSONObject()
                    .put("action", "join")
                    .put("roomName", roomName)
                    .put("username", username)
            );
            updateUserList(roomToJoin);
            updateQuestionList(roomToJoin);
        }
    }

    public void leaveRoom(Session user, String username) throws IOException, JSONException {
        GameRoom roomToLeave = null;
        for (GameRoom room : rooms) {
            if (room.hasUser(user)) {
                roomToLeave = room;
            }
        }
        if(roomToLeave != null) {
            if (user.equals(roomToLeave.getHost())) {
                roomToLeave.notifyAllUsers(new JSONObject()
                        .put("action", "leave")
                );
                Map<Session, String> users = roomToLeave.removeAllUsers();
                lobby.addUsers(users);
                rooms.remove(roomToLeave);
                updateRoomList();
            }
            else {
                roomToLeave.removeUser(user);
                lobby.addUser(user, username);
                notifyUser(user, new JSONObject()
                        .put("action", "leave")
                );
                updateUserList(roomToLeave);
            }
        }
    }

    private void updateUserList(GameRoom room) throws JSONException, IOException {
        room.notifyAllUsers(new JSONObject()
                .put("action", "listUsers")
                .put("users", room.getUsers())
        );
    }

    public void addQuestion(String question, String roomName) throws IOException, JSONException {
        GameRoom room = getRoomByName(roomName);
        if (room != null) {
            room.addQuestion(question);
            updateQuestionList(room);
        }
    }

    private void updateQuestionList(GameRoom room) throws JSONException, IOException {
        room.notifyAllUsers(new JSONObject()
                .put("action", "questionList")
                .put("questionList", room.getQuestions())
        );
    }

    public void startGame(String roomName) throws JSONException, IOException {
        GameRoom room = getRoomByName(roomName);
        if (room != null) {
            if (room.getUsers().size() <= 1) {
                notifyUser(room.getHost(), new JSONObject()
                        .put("action", "startGame")
                        .put("result", "failure")
                        .put("details", "Nie mozesz grac sam")
                );
            }
            else if (room.getQuestions().size() == 0) {
                notifyUser(room.getHost(), new JSONObject()
                        .put("action", "startGame")
                        .put("result", "failure")
                        .put("details", "Musisz dodac chociaz jedno pytanie")
                );
            }
            else {
                room.startGame();
                room.notifyAllUsers(new JSONObject()
                        .put("action", "startGame")
                        .put("result", "success")
                        .put("question", room.getQuestions().get(0))
                );
                updateRoomList();
            }
        }
    }

    public void newQuestion(Session user, String question, String username) throws IOException, JSONException {
        GameRoom room = getRoomByUser(user);
        room.newQuestion(username, room.getQuestions().get(room.getRound()) + " " + question + "?");
        if (room.endAsking()) {
            room.sendPreviousQuestions();
        }
        else {
            room.notifyUser(user, new JSONObject()
                    .put("action", "acceptQuestion")
                    .put("result", "failure")
                    .put("message", "Zaczekaj na pozostalych graczy...")
            );
        }
    }

    public void newAnswer(Session user, String answer, String username) throws JSONException, IOException {
        GameRoom room = getRoomByUser(user);
        room.newAnswer(username, answer);
        if (room.endRound()) {
            if (room.endGame()) {
                endGame(room);
            }
            else {
                room.notifyAllUsers(new JSONObject()
                        .put("action", "acceptAnswer")
                        .put("result", "success")
                        .put("question", room.getQuestions().get(room.getRound()))
                );
            }
        }
        else {
            room.notifyUser(user, new JSONObject()
                    .put("action", "acceptQuestion")
                    .put("result", "failure")
                    .put("message", "Zaczekaj na pozostalych graczy...")
            );
        }
    }

    private void endGame(GameRoom room) throws JSONException, IOException {
        room.notifyAllUsers(new JSONObject()
                .put("action", "endGame")
        );
        room.sendResults();
        room.sendAllResults();
        lobby.addUsers(room.removeAllUsers());
        rooms.remove(room);
    }

    public void notifyUser(Session user, JSONObject notification) throws IOException {
        lobby.notifyUser(user, notification);
        for (Room room : rooms) {
            room.notifyUser(user, notification);
        }
    }

    public void notifyAllUsers(JSONObject notification) throws IOException {
        lobby.notifyAllUsers(notification);
        for (Room room : rooms) {
            room.notifyAllUsers(notification);
        }
    }

    private GameRoom getRoomByName(String name) {
        GameRoom room = null;
        for (GameRoom gameRoom : rooms) {
            if (gameRoom.getName().equals(name)) {
                room = gameRoom;
            }
        }
        return room;
    }

    private GameRoom getRoomByUser(Session user) {
        GameRoom room = null;
        for (GameRoom gameRoom : rooms) {
            if (gameRoom.hasUser(user)) {
                room = gameRoom;
            }
        }
        return room;
    }
}
