//import org.eclipse.jetty.websocket.api.Session;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Created by jakub.a.kret@gmail.com on 2017-02-20.
// */
//public class GameRoom implements Room {
//    private String name;
//    private Session host;
//    private Map<Session, String> users;
//    private List<String> questions;
//    private GameInstance game;
//
//
//    public GameRoom(String name, Session host) {
//        this.name = name;
//        this.host = host;
//        this.users = new ConcurrentHashMap<>();
//        this.questions = new LinkedList<>();
//        this.game = null;
//    }
//
//    @Override
//    public String toString() {
//        return "GameRoom{" +
//                "name='" + name + '\'' +
//                ", \nhost=" + host +
//                ", \nusers=" + users.size() +
//                '}';
//    }
//
//    @Override
//    public String getName() {
//        return this.name;
//    }
//
//    public Session getHost() {
//        return host;
//    }
//
//    @Override
//    public boolean hasUser(Session user) {
//        return users.keySet().contains(user);
//    }
//
//    @Override
//    public boolean usernameTaken(String username) {
//        for (String user : users.values()) {
//            if (user.equals(username)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void notifyUser(Session user, JSONObject notification) throws IOException {
//        if(hasUser(user)) {
//            user.getRemote().sendString(String.valueOf(notification));
//        }
//    }
//
//    @Override
//    public void notifyAllUsers(JSONObject notification) throws IOException {
//        for(Session user : users.keySet()) {
//            if(user.isOpen() && hasUser(user)) {
//                user.getRemote().sendString(String.valueOf(notification));
//            }
//        }
//    }
//
//    @Override
//    public void removeUser(Session user) {
//        if(hasUser(user)) {
//            users.remove(user);
//        }
//    }
//
//    @Override
//    public void addUser(Session user, String name) {
//        users.put(user, name);
//    }
//
//    public Map<Session, String> removeAllUsers() {
//        Map<Session, String> userList = new ConcurrentHashMap<>();
//        for (Session user : users.keySet()) {
//            userList.put(user, users.get(user));
//            users.remove(user);
//        }
//        return userList;
//    }
//
//    public List<String> getUsers() {
//        List<String> userList = new LinkedList<>();
//        for (Session user : users.keySet()) {
//            userList.add(users.get(user));
//        }
//        return userList;
//    }
//
//    public void addQuestion(String question) {
//        this.questions.add(question);
//    }
//
//    public List<String> getQuestions() {
//        return questions;
//    }
//
//    public void newQuestion(String player, String question) {
//        this.game.addQuestion(player, question);
//    }
//
//    public void newAnswer(String player, String answer) {
//        this.game.addAnswer(player, answer);
//    }
//
//    public boolean endAsking() {
//        return this.game.endAsking();
//    }
//
//    public boolean endRound() {
//        return this.game.endRound();
//    }
//
//    public void sendPreviousQuestions() throws IOException, JSONException {
//        for (Session user : users.keySet()) {
//            notifyUser(user, new JSONObject()
//                    .put("action", "acceptQuestion")
//                    .put("result", "success")
//                    .put("question", game.getQuestion(getRound(), users.get(user)))
//            );
//        }
//    }
//
//    public int getRound() {
//        return this.game.getRound();
//    }
//
//    public void startGame() {
//        List<String> players = new LinkedList<>(this.users.values());
//        this.game = new GameInstance(players, questions.size());
//    }
//
//    public boolean gameInProgress() {
//        return game != null;
//    }
//
//    public boolean endGame() {
//        return this.game.endGame();
//    }
//
//    public void sendResults() throws IOException, JSONException {
//        for (Session player : users.keySet()) {
//            notifyUser(player, new JSONObject()
//                    .put("action", "listResults")
//                    .put("result", game.getPlayerQuestions(users.get(player)))
//            );
//        }
//    }
//
//    public void sendAllResults() throws JSONException, IOException {
//        List<JSONObject> objects = new LinkedList<>();
//        for (Session player : users.keySet()) {
//            objects.addAll(game.getPlayerQuestions(users.get(player)));
//        }
//        notifyAllUsers(new JSONObject()
//                .put("action", "listAllResults")
//                .put("result", objects)
//        );
//    }
//
//}