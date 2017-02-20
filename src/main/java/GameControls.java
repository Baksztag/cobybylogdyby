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
        this.rooms = new LinkedList<GameRoom>();
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
        List<String> roomNames = new LinkedList<String>();
        for (Room room : rooms) {
            roomNames.add(room.getName());
        }
        notifyAllUsers(new JSONObject()
                .put("action", "roomList")
                .put("rooms", roomNames));
    }

    public void joinRoom(Session user, String roomName, String username) throws JSONException, IOException {
        GameRoom roomToJoin = null;
        for (GameRoom room : rooms) {
            if (room.getName().equals(roomName)) {
                roomToJoin = room;
            }
        }
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
        GameRoom room = null;
        for (GameRoom gameRoom : rooms) {
            if (gameRoom.getName().equals(roomName)) {
                room = gameRoom;
            }
        }
        System.out.println(room);
        if (room != null) {
            room.addQuestion(question);
            updateQuestionList(room);
            System.out.println(room.getQuestions());
        }
    }

    private void updateQuestionList(GameRoom room) throws JSONException, IOException {
        room.notifyAllUsers(new JSONObject()
                .put("action", "questionList")
                .put("questionList", room.getQuestions())
        );
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
}
