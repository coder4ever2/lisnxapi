package nayax.Enum

/**
 * Created by IntelliJ IDEA.
 * User: gaurav
 * Date: 2/26/12
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ProfileShareType {

    PROFESSIONAL("Professional"), CASUAL("Casual"), EMAIL("Email"), ALL('All')

    private final String str;

    ProfileShareType(String str) {
        this.str = str;
    }

    public String getKey() { return name() }

    public String getValue() { return toString() }

    public String toString() {
        return str;
    }

    public static list() {
        List profileShareTypes = []
        ProfileShareType.values().each {ProfileShareType profileShareType ->
            profileShareTypes.add(profileShareType.getKey())
        }
        return profileShareTypes
    }
}