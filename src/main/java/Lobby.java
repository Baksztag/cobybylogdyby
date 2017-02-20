import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class Lobby implements Room {
    @Override
    public boolean hasUser(Session user) {
        return false;
    }

    @Override
    public void notifyUser(Session user, JSONObject notification) {

    }

    @Override
    public void notifyAllUsers(JSONObject notification) {

    }

    @Override
    public void removeUser(Session user) {

    }

    @Override
    public void addUser(Session user) {

    }
}
