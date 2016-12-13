package nayax.Enum

/**
 * Created by IntelliJ IDEA.
 * User: gaurav
 * Date: 25/8/11
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public enum NConnectionStatus {
    PENDING("PENDING"), IGNORED("IGNORED"), CONNECTED("CONNECTED")

    private final String str;

    NConnectionStatus(String str) {
        this.str = str;
    }

    public String getKey() { return name() }

    public String getValue() { return toString() }

    public String toString() {
        return str;
    }
}