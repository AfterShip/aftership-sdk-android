package Enums;

/**
 * Created by User on 18/6/14.
 */
public enum ConnectionAPIMethods {

    getLastCheckpoint(0),reactivate(1),getTrackingByNumber(2),getTracking(3),deleteTracking(4),
    postTracking(5),putTracking(6),getCouriers(7),detectCouriers(8);
    private final int numberMethod;

    ConnectionAPIMethods(int numberMethod){
        this.numberMethod = numberMethod;
    }

    public int getNumberMethod(){

        return this.numberMethod;
    }
}
