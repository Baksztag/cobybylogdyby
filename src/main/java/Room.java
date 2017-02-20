import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public interface Room {
    boolean hasUser(Session user);
    boolean usernameTaken(String username);
    void notifyUser(Session user, JSONObject notification) throws IOException;
    void notifyAllUsers(JSONObject notification) throws IOException;
    void removeUser(Session user);
    void addUser(Session user, String name);
}
