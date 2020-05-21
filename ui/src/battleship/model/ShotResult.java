package battleship.model;

import java.io.Serializable;

public class ShotResult implements Serializable {
    private static final long serialVersionUID = -867466966173959835L;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public enum Response {
        HIT,
        MISS,
        SUNK,
        SUNK_ALL
    }
}
