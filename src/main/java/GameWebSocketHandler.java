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
    }


    private void updateRoomList() {
//        controls.ge
    }

//
//    private void notifyUsers(Map<Session, Session> users, JSONObject notification) throws IOException {
//        for(Session user : users.keySet()) {
//            if(user.isOpen()) {
//                user.getRemote().sendString(String.valueOf(notification));
//            }
//        }
//    }
}
