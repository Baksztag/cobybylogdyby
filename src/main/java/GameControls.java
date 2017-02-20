import org.eclipse.jetty.websocket.api.Session;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
public class GameControls {
    private List<Map<Session, String>> rooms;
    private List<String> roomNames;
    private Map<Session, String> lobby;


    public GameControls() {
        this.rooms = new LinkedList<Map<Session, String>>();
        this.roomNames = new LinkedList<String>();
        this.lobby = new ConcurrentHashMap<Session, String>();
    }

    public void addRoom(String name) {

    }
}
