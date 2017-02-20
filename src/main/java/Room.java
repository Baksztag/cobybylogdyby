import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public interface Room {
    boolean hasUser(Session user);
    void notifyUser(Session user, JSONObject notification);
    void notifyAllUsers(JSONObject notification);
    void removeUser(Session user);
    void addUser(Session user);
}
