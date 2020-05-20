package battleship.message;

import java.io.Serializable;

public class GreetingsMessage implements Serializable {
    private static final long serialVersionUID = 6775995533049138997L;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
