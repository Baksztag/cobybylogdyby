import java.util.LinkedList;
import java.util.List;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class PlayerDetails {
    public final String username;
    private List<Question> questions;


    public PlayerDetails(String username) {
        this.username = username;
        questions = new LinkedList<>();
    }
}
