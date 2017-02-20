/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class Request {
    private String action;
    private String roomName;
    private String username;


    @Override
    public String toString() {
        return "Request{" +
                "action='" + action + '\'' +
                ", roomName='" + roomName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
