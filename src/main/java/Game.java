import org.eclipse.jetty.util.ConcurrentHashSet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-03-06.
 */
public class Game {
    private String room;
    private String host;
    private UserManager users;
    private Map<String, Integer> playerIndexMap;
    private QuestionContainer[] questions;
    private int round;
    private boolean inProgress;
    private List<String> questionPrefixes;
    private Set<String> disconnectedPlayers;


    public Game(UserManager users, String room, String host) {
        this.room = room;
        this.host = host;
        this.users = users;
        this.round = 0;
        this.inProgress = false;
        this.questionPrefixes = new LinkedList<>();
    }

    public String getHost() {
        return host;
    }

    public int getRound() {
        return round;
    }

    public boolean isInProgress() {
        return this.inProgress;
    }

    public void addQuestionPrefix(String question) {
        if (!this.inProgress) {
            this.questionPrefixes.add(question);
        }
    }

    public void removeQuestionPrefix(String question) {
        if (!this.inProgress) {
            this.questionPrefixes.remove(question);
        }
    }

    public int numberOfQuestions() {
        return this.questionPrefixes.size();
    }

    public List<String> getQuestionPrefixes() {
        return this.questionPrefixes;
    }

    public void startGame() throws JSONException, IOException {
        Set<String> players = users.getUsersInRoom(this.room);
        this.inProgress = true;
        this.playerIndexMap = new ConcurrentHashMap<>();
        this.disconnectedPlayers = new ConcurrentHashSet<>();
        int p = 0;
        for (String player : players) {
            this.playerIndexMap.put(player, p);
            p++;
        }
        questions = new QuestionContainer[questionPrefixes.size()];
        for (int i = 0; i < questions.length; i++) {
            questions[i] = new QuestionContainer(players.size());
        }
        users.messageUsersInRoom(room, new JSONObject()
                .put("action", "startGame")
                .put("result", "success")
                .put("question", questionPrefixes.get(this.round))
        );
    }

    public void addQuestion(String playerName, String question) {
        if (this.inProgress) {
            this.questions[this.round].addQuestion(this.playerIndexMap.get(playerName),
                    this.questionPrefixes.get(this.round) + " " + question);
        }
    }

    public void addAnswer(String playerName, String answer) {
        if (this.inProgress) {
            this.questions[this.round].addAnswer(this.playerIndexMap.get(playerName), answer);
        }
    }

    public String getQuestion(int round, String username) {
        if (this.inProgress) {
            return questions[round].getQuestion(playerIndexMap.get(username));
        }
        return "";
    }

    public boolean endAsking() {
        return this.questions[round].questionsComplete();
    }

    public boolean endRound() {
        if (this.questions[round].isComplete()) {
            questions[round].switchAnswers();
            round++;
            return true;
        }
        return false;
    }

    public boolean endGame() {
        if (round == questions.length) {
            this.inProgress = false;
            return true;
        }
        return false;
    }

    public List<JSONObject> getPlayerQuestions(String player) throws JSONException {
        List<JSONObject> questions = new LinkedList<>();
        for (int i = 0; i < this.questions.length; i++) {
            questions.add(this.questions[i].getQuestionPair((playerIndexMap.get(player) + i) % playerIndexMap.size()).toJSON());
        }
        return questions;
    }

    public void sendPreviousQuestions() throws JSONException, IOException {
        for (String player : playerIndexMap.keySet()) {
            if (!disconnectedPlayers.contains(player)) {
                users.messageUser(player, this.room, new JSONObject()
                        .put("action", "acceptQuestion")
                        .put("result", "success")
                        .put("question", getQuestion(getRound(), player))
                );
            }
        }
    }

    public void waitForPlayers(String username) throws JSONException, IOException {
        users.messageUser(username, this.room, new JSONObject()
                .put("action", "acceptQuestion")
                .put("result", "failure")
                .put("message", "Zaczekaj na pozostalych graczy...")
        );
    }

    public void sendEndGame() throws JSONException, IOException {
        users.messageUsersInRoom(this.room, new JSONObject()
                .put("action", "endGame")
        );
    }

    public void sendResults() throws IOException, JSONException {
        for (String player : playerIndexMap.keySet()) {
            if (!disconnectedPlayers.contains(player)) {
                users.messageUser(player, this.room, new JSONObject()
                        .put("action", "listResults")
                        .put("result", getPlayerQuestions(player))
                );
            }
        }
    }

    public void sendNextQuestionPrefix() throws JSONException, IOException {
        users.messageUsersInRoom(this.room, new JSONObject()
                .put("action", "acceptAnswer")
                .put("result", "success")
                .put("question", questionPrefixes.get(this.round))
        );
    }

    public void sendAllResults() throws JSONException, IOException {
        List<JSONObject> objects = new LinkedList<>();
        for (String player : playerIndexMap.keySet()) {
            objects.addAll(getPlayerQuestions(player));
        }
        users.messageUsersInRoom(this.room, new JSONObject()
                .put("action", "listAllResults")
                .put("result", objects)
        );
    }

    public void dropPlayer(String username) {
        for (int i = round; i < questions.length; i++) {
            questions[i].addQuestion(playerIndexMap.get(username),
                    "-?");
            questions[i].addAnswer(playerIndexMap.get(username), "-");
        }
//        playerIndexMap.remove(username);
        disconnectedPlayers.add(username);
    }
}
