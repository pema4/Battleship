package battleship.models;

import battleship.basics.Ship;

import java.io.Serializable;

/**
 * Сообщение, оправляемое игроку, запрашивающему выстрел
 * Замечание: корабль отправляется только тогда, когда он потоплен. Так что игроки не могут смухлевать
 */
public class ShotResponse implements Serializable {
    private static final long serialVersionUID = 6530813119888438577L;
    private final Type type;
    private final Ship sunkShip; // корабль отправляется только тогда, когда он потоплен
    private final ShotRequest request;

    private ShotResponse(Type type, ShotRequest request, Ship sunkShip) {
        this.request = request;
        this.type = type;
        this.sunkShip = sunkShip;
    }

    public Type getType() {
        return type;
    }

    public Ship getSunkShip() {
        return sunkShip;
    }

    public ShotRequest getRequest() {
        return request;
    }

    public enum Type {
        HIT,
        MISS,
        SUNK,
        SUNK_ALL
    }

    public static ShotResponse miss(ShotRequest request) {
        return new ShotResponse(Type.MISS, request, null);
    }

    public static ShotResponse hit(ShotRequest request) {
        return new ShotResponse(Type.HIT, request, null);
    }

    public static ShotResponse sunk(ShotRequest request, Ship sunkShip) {
        return new ShotResponse(Type.SUNK, request, sunkShip);
    }

    public static ShotResponse sunkAll(ShotRequest request, Ship sunkShip) {
        return new ShotResponse(Type.SUNK_ALL, request, sunkShip);
    }
}
