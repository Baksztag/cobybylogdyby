import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class GameRoom implements Room {
    @Override
    public boolean hasUser(Session user) {
        return false;
    }

    @Override
    public void notifyUser(Session user, JSONObject notification) throws IOException {

    }

    @Override
    public void notifyAllUsers(JSONObject notification) throws IOException {

    }

    @Override
    public void removeUser(Session user) {

    }

    @Override
    public void addUser(Session user, String name) {

    }
}
