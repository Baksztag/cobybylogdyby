import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
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
            switch (req.getAction()) {
                case "newUser":
                    controls.newUser(user, req.getUsername());
                    break;
                case "newRoom":
                    controls.addRoom(req.getRoomName(), user, req.getUsername());
                    break;
                case "join":
                    controls.joinRoom(user, req.getRoomName(), req.getUsername());
                    break;
                case "leave":
                    controls.leaveRoom(user, req.getUsername());
                    break;
                case "newQuestion":
                    controls.addQuestion(req.getQuestion(), req.getRoomName());
                    break;
                case "startGame":
                    controls.startGame(req.getRoomName());
                    break;
                case "acceptQuestion":
                    controls.newQuestion(user, req.getQuestion(), req.getUsername());
                    break;
                case "acceptAnswer":
                    controls.newAnswer(user, req.getAnswer(), req.getUsername());
                    break;
                case "ping":
                    controls.pong(user);
                    break;
                case "close":
                    controls.close(user, req.getUsername());
                    user.close();
                default:
                    System.out.println("Unsupported requested action '" + req.getAction() + "'");
                    break;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("closed");
        System.out.println(statusCode);
        System.out.println(reason);
    }
}
