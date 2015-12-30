package protocol;

/**
 * Created by Юлия on 27.12.2015.
 */
public class Message {
    String nickName;
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
