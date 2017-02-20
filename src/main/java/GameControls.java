import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
public class GameControls {
    private Lobby lobby;
    private List<Room> rooms;


    public GameControls() {
        this.rooms = new LinkedList<>();
        this.lobby = new Lobby();
    }

    private boolean usernameTaken(String username) {
        boolean usernameInRooms = false;
        for(Room room : rooms) {
            if(room.usernameTaken(username))
                usernameInRooms = true;
        }
        return lobby.usernameTaken(username) || usernameInRooms;
    }

    public void newUser(Session user, String username) throws JSONException, IOException {
        if(!usernameTaken(username)) {
            lobby.addUser(user, username);
            user.getRemote().sendString(String.valueOf(
                    new JSONObject()
                            .put("action", "newUser")
                            .put("result", "success")
                            .put("username", username)
            ));
        }
        else {
            user.getRemote().sendString(String.valueOf(
                    new JSONObject()
                            .put("action", "newUser")
                            .put("result", "username taken")
                            .put("username", username)
            ));
        }
    }

    public void addRoom(String name) {

    }
}
