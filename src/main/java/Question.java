/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class Question {
    private final String question;
    private String answer;


    public Question(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
