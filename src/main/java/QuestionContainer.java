import java.util.Arrays;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-21.
 */
public class QuestionContainer {
    private Question[] questions;


    public QuestionContainer(int numberOfQuestions) {
        this.questions = new Question[numberOfQuestions];
    }

    @Override
    public String toString() {
        return "QuestionContainer{" +
                "questions=" + Arrays.toString(questions) +
                '}';
    }

    public void addQuestion(int playerIndex, String question) {
        this.questions[playerIndex] = new Question(question);
    }

    public void addAnswer(int playerIndex, String answer) {
        this.questions[playerIndex].setAnswer(answer);
    }

    public String getQuestion(int playerIndex) {
        return questions[playerIndex].getQuestion();
    }

    public Question getQuestionPair(int playerIndex) {
        return questions[playerIndex];
    }

    public boolean questionsComplete() {
        for (int i = 0; i < questions.length; i++) {
            if (questions[i] == null || questions[i].getQuestion() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isComplete() {
        for (int i = 0; i < questions.length; i++) {
            if (questions[i] == null || questions[i].getAnswer() == null) {
                return false;
            }
        }
        return true;
    }

    public void switchAnswers() {
        String next = questions[0].getAnswer();
        String tmp;
        for(int i = 0; i < questions.length - 1; i++) {
            tmp = questions[i + 1].getAnswer();
            questions[i + 1].setAnswer(next);
            next = tmp;
        }
        questions[0].setAnswer(next);
    }
}
