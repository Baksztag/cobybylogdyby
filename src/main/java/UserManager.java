import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-03-04.
 */
public class UserManager {
    private Map<String, Map<String, Session>> rooms;


    public UserManager() {
        this.rooms = new ConcurrentHashMap<>();
        this.rooms.put("lobby", new ConcurrentHashMap<>());
    }

    public boolean roomNameTaken(String roomName) {
        if (this.rooms.containsKey(roomName)) {
            return true;
        }
        return false;
    }

    public void newRoom(String roomName) {
        this.rooms.put(roomName, new ConcurrentHashMap<>());
    }

    public boolean usernameTaken(String username) {
        for (Map<String, Session> room : rooms.values()) {
            if (room.containsKey(username)) {
                return true;
            }
        }
        return false;
    }

    public void newUser(Session user, String username) {
        this.rooms.get("lobby").put(username, user);
    }

    public void removeUser(String username) {
        this.rooms.values().forEach(room -> room.remove(username));
    }

    public void addUserToRoom(String username, Session user, String roomName) {
        this.rooms.get(roomName).put(username, user);
    }

    public void removeUserFromRoom(String roomName, String username) {
        this.rooms.get(roomName).remove(username);
    }

    public void removeRoom(String room) {
        rooms.get("lobby").putAll(rooms.get(room));
        rooms.get(room).clear();
        rooms.remove(room);
    }

    public Set<String> getUsersInRoom(String room) {
        return this.rooms.get(room).keySet();
    }

    public Set<String> getRooms() {
        return this.rooms.keySet();
    }

    public int usersInRoom(String room) {
        return this.rooms.get(room).size();
    }

    public void messageUser(String username, String roomName, JSONObject message) throws IOException {
        if (rooms.get(roomName).get(username) != null) {
            if (rooms.get(roomName).get(username).isOpen()) {
                rooms.get(roomName).get(username).getRemote().sendString(String.valueOf(message));
            }
        }
    }

    public String getRoomByUser(String username) {
        for (String room : rooms.keySet()) {
            if (rooms.get(room).containsKey(username)) {
                return room;
            }
        }
        return null;
    }

    public Session getSessionByUser(String username) {
        for (Map<String, Session> room : rooms.values()) {
            if (room.containsKey(username)) {
                return room.get(username);
            }
        }
        return null;
    }

    public void messageUsersInRoom(String roomName, JSONObject message) throws IOException {
        for (Session user : rooms.get(roomName).values()) {
            if (user.isOpen()) {
                user.getRemote().sendString(String.valueOf(message));
            }
        }
    }
}
