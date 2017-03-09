import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jakub.a.kret@gmail.com on 2017-03-09.
 */
public class ResponseManager {
    private UserManager users;
    private GameManager games;


    public ResponseManager(UserManager users, GameManager games) {
        this.users = users;
        this.games = games;
    }

    public void newUserError(Session user, String username) throws JSONException, IOException {
        if (user.isOpen()) {
            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("action", "newUser")
                    .put("result", "error")
                    .put("username", username)));
        }
    }

    public void newUserSuccess(Session user, String username) throws JSONException, IOException {
        if (user.isOpen()) {
            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("action", "newUser")
                    .put("result", "success")
                    .put("username", username)));
        }
    }

    public void newRoomError(String username, String roomName) throws JSONException, IOException {
        users.messageUser(username, "lobby", new JSONObject()
                .put("action", "newRoom")
                .put("result", "error")
                .put("roomName", roomName));
    }

    public void newRoomSuccess(String username, String roomName) throws JSONException, IOException {
        users.messageUser(username, roomName, new JSONObject()
                .put("action", "newRoom")
                .put("result", "success")
                .put("roomName", roomName));
    }

    public void updateRoomList(String username) throws JSONException, IOException {
        List<String> rooms = new LinkedList<>();
        for (String room : users.getRooms()) {
            if (!games.gameInProgress(room) && !room.equals("lobby")) {
                rooms.add(room);
            }
        }
        users.messageUser(username, "lobby", new JSONObject()
                .put("action", "roomList")
                .put("rooms", rooms)
        );
    }

    public void updateRoomList() throws JSONException, IOException {
        List<String> rooms = new LinkedList<>();
        for (String room : users.getRooms()) {
            if (!games.gameInProgress(room) && !room.equals("lobby")) {
                rooms.add(room);
            }
        }
        for (String room : users.getRooms()) {
            users.messageUsersInRoom(room, new JSONObject()
                    .put("action", "roomList")
                    .put("rooms", rooms)
            );
        }
    }

    public void updateUserList(String room) throws JSONException, IOException {
        users.messageUsersInRoom(room, new JSONObject()
                .put("action", "listUsers")
                .put("users", users.getUsersInRoom(room))
        );
    }

    public void joinRoomSuccess(String username, String roomName) throws JSONException, IOException {
        users.messageUser(username, roomName, new JSONObject()
                .put("action", "join")
                .put("roomName", roomName)
                .put("username", username)
        );
    }

    public void updateQuestionList(String roomName) throws JSONException, IOException {
        users.messageUsersInRoom(roomName, new JSONObject()
                .put("action", "questionList")
                .put("questionList", games.getCurrentQuestions(roomName))
        );
    }

    public void updateQuestionList(String username, String roomName) throws JSONException, IOException {
        users.messageUser(username, roomName, new JSONObject()
                .put("action", "questionList")
                .put("questionList", games.getCurrentQuestions(roomName))
        );
    }

    public void hostLeftTheRoom(String roomName) throws JSONException, IOException {
        users.messageUsersInRoom(roomName, new JSONObject()
                .put("action", "leave")
        );
    }

    public void leaveRoom(String username, String roomName) throws JSONException, IOException {
        users.messageUser(username, roomName, new JSONObject()
                .put("action", "leave")
        );
    }

    public void ping(String username, String roomName) throws JSONException, IOException {
        users.messageUser(username, roomName, new JSONObject()
                .put("action", "ping")
        );
    }

    public void dropPlayer(String username, String roomName) throws JSONException, IOException {
        if (!roomName.equals("lobby")) {
            users.messageUsersInRoom(roomName, new JSONObject()
                    .put("action", "playerDisconnected")
                    .put("username", username)
            );
        }
    }
}
