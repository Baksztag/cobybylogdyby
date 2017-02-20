import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

    public void addRoom(String name, Session host) throws JSONException, IOException {
        if (!roomNameTaken(name)) {
            GameRoom newRoom = new GameRoom(name, host);
            rooms.add(newRoom);
            updateRoomList();
            notifyUser(host, new JSONObject()
                    .put("action", "newRoom")
                    .put("result", "success")
                    .put("roomName", name)
            );
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

    public void updateRoomList() throws JSONException, IOException {
        List<String> roomNames = new LinkedList<>();
        for (Room room : rooms) {
            roomNames.add(room.getName());
        }
        notifyAllUsers(new JSONObject()
                .put("action", "roomList")
                .put("rooms", roomNames));
    }
}
