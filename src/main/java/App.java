import static spark.Spark.*;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        webSocket("/game", GameWebSocketHandler.class);
        init();
    }
}
