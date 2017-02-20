import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
@WebSocket
public class GameWebSocketHandler {
    private GameControls controls = new GameControls();

    @OnWebSocketConnect
    public void onConnect(Session user) {

    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println(message);
        Gson gson = new Gson();
        Request req = gson.fromJson(message, Request.class);

        try {
            if (req.getAction().equals("newUser")) {
                controls.newUser(user, req.getUsername());
            }
            else if (req.getAction().equals("newRoom")) {
                controls.addRoom(req.getRoomName(), user);
            }
            else if (req.getAction().equals("join")) {
                controls.joinRoom(user, req.getRoomName(), req.getUsername());
            }


            else {
                System.out.println("Unsupported requested action '" + req.getAction() + "'");

            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
