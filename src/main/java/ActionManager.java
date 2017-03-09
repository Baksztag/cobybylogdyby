import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

/**
 * Created by jakub.a.kret@gmail.com on 2017-03-09.
 */
public class ActionManager {
    private UserManager users;
    private GameManager games;
    private ResponseManager response;


    public ActionManager(UserManager users, GameManager games, ResponseManager response) {
        this.users = users;
        this.games = games;
        this.response = response;
    }

    public void newUser(String username, Session user) throws IOException, JSONException {
        if (users.usernameTaken(username)) {
            response.newUserError(user, username);
        } else {
            users.newUser(user, username);
            response.newUserSuccess(user, username);
            response.updateRoomList(username);
        }
    }

    public void newRoom(String username, String roomName, Session user) throws IOException, JSONException {
        if (users.roomNameTaken(roomName)) {
            response.newRoomError(username, roomName);
        } else {
            users.newRoom(roomName);
            games.newGame(roomName, username);
            users.removeUserFromRoom("lobby", username);
            users.addUserToRoom(username, user, roomName);
            response.newRoomSuccess(username, roomName);
            response.updateUserList(roomName);
            response.updateRoomList();
        }
    }

    public void joinRoom(String username, String roomName, Session user) throws IOException, JSONException {
        users.removeUserFromRoom("lobby", username);
        users.addUserToRoom(username, user, roomName);
        response.joinRoomSuccess(username, roomName);
        response.updateUserList(roomName);
        response.updateQuestionList(username, roomName);
    }

    public void leaveRoom(String username, String roomName, Session user) throws IOException, JSONException {
        if (username.equals(games.getHost(roomName))) {
            response.hostLeftTheRoom(roomName);
            users.removeRoom(roomName);
            games.removeGame(roomName);
            response.updateRoomList();
        } else {
            response.leaveRoom(username, roomName);
            users.removeUserFromRoom(roomName, username);
            users.addUserToRoom(username, user, "lobby");
            response.updateUserList(roomName);
        }
    }

    public void newQuestionPrefix(String roomName, String questionPrefix) throws IOException, JSONException {
        games.addQuestion(questionPrefix, roomName);
        response.updateQuestionList(roomName);
    }

    public void startGame(String roomName) throws IOException, JSONException {
        games.startGame(roomName);
        response.updateRoomList();
    }

    public void dropPlayer(String username, String roomName) throws IOException, JSONException {
        if (!roomName.equals("lobby")) {
            if (!games.gameInProgress(roomName)) {
                Session user = users.getSessionByUser(username);
                if (user != null) {
                    leaveRoom(username, roomName, user);
                    users.removeUser(username);
                }
            } else {
                games.dropPlayer(username, roomName);
                users.removeUser(username);
            }
        }
    }
}
