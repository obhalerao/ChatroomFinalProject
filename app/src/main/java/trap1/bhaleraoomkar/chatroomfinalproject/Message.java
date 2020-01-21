package trap1.bhaleraoomkar.chatroomfinalproject;

public class Message {
    private String name;
    private String message;

    public Message(String name, String message){
        this.name=name;
        this.message=message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
