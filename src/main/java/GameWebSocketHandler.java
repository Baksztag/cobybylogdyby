import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Date;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
@WebSocket
public class GameWebSocketHandler {
    //    private GameControls controls = new GameControls();
    private UserManager users = new UserManager();
    private GameManager games = new GameManager(users);
    private ResponseManager response = new ResponseManager(users, games);
    private ActionManager action = new ActionManager(users, games, response);
    private ConnectionManager connections = new ConnectionManager(users, action, response);


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
                    action.newUser(req.getUsername(), user);
                    connections.newUser(req.getUsername(), new Date().getTime());
                    break;
                case "newRoom":
                    action.newRoom(req.getUsername(), req.getRoomName(), user);
                    break;
                case "join":
                    action.joinRoom(req.getUsername(), req.getRoomName(), user);
                    break;
                case "leave":
                    action.leaveRoom(req.getUsername(), req.getRoomName(), user);
                    break;
                case "newQuestion":
                    action.newQuestionPrefix(req.getRoomName(), req.getQuestion());
                    break;
                case "startGame":
                    action.startGame(req.getRoomName());
                    break;
                case "acceptQuestion":
                    games.newQuestion(req.getRoomName(), req.getQuestion(), req.getUsername());
                    break;
                case "acceptAnswer":
                    games.newAnswer(req.getRoomName(), req.getAnswer(), req.getUsername());
                    break;
                case "pong":
                    connections.pong(req.getUsername(), new Date().getTime());
                    break;
                default:
                    System.out.println("Unsupported requested action '" + req.getAction() + "'");
                    break;
            }
            connections.updateActivityDate(req.getUsername(), new Date().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {

    }
}
