/**
 * Created by jakub.a.kret@gmail.com on 2017-02-20.
 */
public class Request {
    private String action;
    private String roomName;


    @Override
    public String toString() {
        return "Request{" +
                "action='" + action + '\'' +
                ", roomName='" + roomName + '\'' +
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
}
