//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by jakub.a.kret@gmail.com on 2017-02-21.
// */
//public class GameInstance {
//    private Map<String, Integer> playerIndexMap;
//    private QuestionContainer[] questions;
//    private int round;
//
//
//    public GameInstance(List<String> players, int numberOfQuestions) {
//        this.round = 0;
//        this.playerIndexMap = new HashMap<>();
//        for (int i = 0; i < players.size(); i++) {
//            playerIndexMap.put(players.get(i), i);
//        }
//        questions = new QuestionContainer[numberOfQuestions];
//        for (int i = 0; i < questions.length; i++) {
//            questions[i] = new QuestionContainer(players.size());
//        }
//    }
//
//    public void addQuestion(String playerName, String question) {
//        this.questions[this.round].addQuestion(this.playerIndexMap.get(playerName), question);
//    }
//
//    public void addAnswer(String playerName, String answer) {
//        this.questions[this.round].addAnswer(this.playerIndexMap.get(playerName), answer);
//    }
//
//    public String getQuestion(int round, String username) {
//        return questions[round].getQuestion(playerIndexMap.get(username));
//    }
//
//    public boolean endAsking() {
//        if (this.questions[round].questionsComplete()) {
//            System.out.println("All questions asked");
//            return true;
//        }
//        return false;
//    }
//
//    public boolean endRound() {
//        if (this.questions[round].isComplete()) {
//            System.out.println("Round complete");
//            questions[round].switchAnswers();
//            round++;
//            return true;
//        }
//        return false;
//    }
//
//    public boolean endGame() {
//        if (round == questions.length) {
//            System.out.println("Game complete");
//            return true;
//        }
//        return false;
//    }
//
//    public List<JSONObject> getPlayerQuestions(String player) throws JSONException {
//        List<JSONObject> questions = new LinkedList<>();
//        for (int i = 0; i < this.questions.length; i++) {
//            questions.add(this.questions[i].getQuestionPair((playerIndexMap.get(player) + i) % playerIndexMap.size()).toJSON());
//        }
//        return questions;
//    }
//
//    public int getRound() {
//        return round;
//    }
//}
