import static spark.Spark.*;

/**
 * Created by jakub.a.kret@gmail.com on 2017-02-18.
 */
public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        webSocket("/game", GameWebSocketHandler.class);
        port(getHerokuAssignedPort());
        init();
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
