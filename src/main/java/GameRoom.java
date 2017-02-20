import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class GameRoom implements Room {
    private Map<Session, PlayerDetails> users;


    @Override
    public boolean hasUser(Session user) {
        return users.keySet().contains(user);
    }

    @Override
    public void notifyUser(Session user, JSONObject notification) throws IOException {
        user.getRemote().sendString(String.valueOf(notification));
    }

    @Override
    public void notifyAllUsers(JSONObject notification) throws IOException {
        for(Session user : users.keySet()) {
            if(user.isOpen()) {
                user.getRemote().sendString(String.valueOf(notification));
            }
        }
    }

    @Override
    public void removeUser(Session user) {
        if(hasUser(user)) {
            users.remove(user);
        }
    }

    @Override
    public void addUser(Session user, String name) {
        users.put(user, new PlayerDetails(name));
    }
}
