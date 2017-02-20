import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class Lobby implements Room {
    private Map<Session, String> users;


    public Lobby() {
        this.users = new ConcurrentHashMap<Session, String>();
    }

    @Override
    public String getName() {
        return "Lobby";
    }

    @Override
    public boolean hasUser(Session user) {
        return users.keySet().contains(user);
    }

    @Override
    public boolean usernameTaken(String username) {
        for(String name : users.values()) {
            if(name.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void notifyUser(Session user, JSONObject notification) throws IOException {
        if(hasUser(user)) {
            user.getRemote().sendString(String.valueOf(notification));
        }
    }

    @Override
    public void notifyAllUsers(JSONObject notification) throws IOException {
        for(Session user : users.keySet()) {
            if(user.isOpen() && hasUser(user)) {
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
        users.put(user, name);
    }
}
