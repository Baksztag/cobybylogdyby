import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-03-06.
 */
public class GameManager {
    private UserManager users;
    private Map<String, Game> games;


    public GameManager(UserManager users) {
        this.users = users;
        this.games = new ConcurrentHashMap<>();
    }

    public void newGame(String room, String host) {
        this.games.put(room, new Game(users, room, host));
    }

    public void addQuestion(String question, String roomName) throws IOException, JSONException {
        if (this.games.containsKey(roomName)) {
            this.games.get(roomName).addQuestionPrefix(question);
        }
    }

    public void startGame(String room) throws JSONException, IOException {
        if (this.games.containsKey(room)) {
            if (users.usersInRoom(room) <= 1) {
                users.messageUser(this.games.get(room).getHost(), room, new JSONObject()
                        .put("action", "startGame")
                        .put("result", "failure")
                        .put("details", "Nie mozesz grac sam"));
            } else if (this.games.get(room).numberOfQuestions() == 0) {
                users.messageUser(this.games.get(room).getHost(), room, new JSONObject()
                        .put("action", "startGame")
                        .put("result", "failure")
                        .put("details", "Musisz dodac chociaz jedno pytanie"));
            } else {
                this.games.get(room).startGame();
//                users.messageUsersInRoom(room, new JSONObject()
//                        .put("action", "startGame")
//                        .put("result", "success")
//                );
            }
        }
    }

    public void newQuestion(String roomName, String question, String username) throws IOException, JSONException {
        Game room = this.games.get(roomName);
        room.addQuestion(username, question);
        if (room.endAsking()) {
            room.sendPreviousQuestions();
        } else {
            room.waitForPlayers(username);
        }
    }

    public void newAnswer(String roomName, String answer, String username) throws JSONException, IOException {
        Game room = games.get(roomName);
        room.addAnswer(username, answer);
        if (room.endRound()) {
            if (room.endGame()) {
                endGame(roomName);
            } else {
                room.sendNextQuestionPrefix();
            }
        } else {
            room.waitForPlayers(username);
        }
    }

    public boolean gameInProgress(String game) {
        if (this.games.get(game) != null) {
            return this.games.get(game).isInProgress();
        }
        return false;
    }

    public List<String> getCurrentQuestions(String game) {
        return games.get(game).getQuestionPrefixes();
    }

    private void endGame(String roomName) throws JSONException, IOException {
        Game room = games.get(roomName);
        room.sendEndGame();
        room.sendResults();
        room.sendAllResults();
        users.removeRoom(roomName);
    }

    public String getHost(String roomName) {
        return this.games.get(roomName).getHost();
    }

    public void removeGame(String game) {
        this.games.remove(game);
    }

    public void dropPlayer(String username, String roomName) throws IOException, JSONException {
        Game room = games.get(roomName);
        room.dropPlayer(username);
        if (room.endAsking() && room.endRound() && room.endGame()) {
            endGame(roomName);
        } else if (room.endAsking() && room.endRound()) {
            room.sendNextQuestionPrefix();
        } else {
            room.sendPreviousQuestions();
        }
    }
}
