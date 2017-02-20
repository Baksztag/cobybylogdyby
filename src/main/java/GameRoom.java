import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class GameRoom implements Room {
    private String name;
    private Session host;
    private Map<Session, PlayerDetails> users;


    public GameRoom(String name, Session host) {
        this.name = name;
        this.host = host;
        this.users = new ConcurrentHashMap<Session, PlayerDetails>();
    }

    @Override
    public String toString() {
        return "GameRoom{" +
                "name='" + name + '\'' +
                ", \nhost=" + host +
                ", \nusers=" + users.size() +
                '}';
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Session getHost() {
        return host;
    }

    @Override
    public boolean hasUser(Session user) {
        return users.keySet().contains(user);
    }

    @Override
    public boolean usernameTaken(String username) {
        for(PlayerDetails player : users.values()) {
            if(player.username.equals(username)) {
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
        users.put(user, new PlayerDetails(name));
    }

    public Map<Session, String> removeAllUsers() {
        Map<Session, String> userList = new ConcurrentHashMap<>();
        for (Session user : users.keySet()) {
            userList.put(user, users.get(user).username);
            users.remove(user);
        }
        return userList;
    }
}
