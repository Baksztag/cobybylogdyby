import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
@WebSocket
public class GameWebSocketHandler {
    @OnWebSocketConnect
    public void onConnect(Session user) {

    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {

    }
}
