import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jakub.a.kret@gmail.com on 2017-03-09.
 */
public class ConnectionManager {
    private UserManager users;
    private ActionManager actions;
    private ResponseManager responses;
    private Map<String, Long> lastActivityDates;


    public ConnectionManager(UserManager users, ActionManager actions, ResponseManager responses) {
        this.users = users;
        this.actions = actions;
        this.responses = responses;
        this.lastActivityDates = new ConcurrentHashMap<>();
        startConnectionMaintenance();
    }

    private void startConnectionMaintenance() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!lastActivityDates.isEmpty()) {
                        for (String user : lastActivityDates.keySet()) {
                            if (new Date().getTime() - lastActivityDates.get(user) > 10 * 1000) {
                                lastActivityDates.remove(user);
                                actions.dropPlayer(user, users.getRoomByUser(user));
                                System.out.println(user + " disconnected");
                            }
                            else if (new Date().getTime() - lastActivityDates.get(user) > 4 * 1000) {
                                responses.ping(user, users.getRoomByUser(user));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 2 * 1000);
    }

    public void newUser(String username, long time) {
        this.lastActivityDates.put(username, time);
    }

    public void updateActivityDate(String username, long date) {
        this.lastActivityDates.put(username, date);
    }

    public void pong(String username, long time) {
        this.lastActivityDates.put(username, time);
    }
}
