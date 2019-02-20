package foundation.bluewhale.splashviews.security;

public class SecurityWrapper {
    public static String encrypt(String text) throws Exception {
        return TripleDesTool.encrypt(text);
    }

    public static String decrypt(String text) throws Exception {
        return TripleDesTool.decrypt(text);
    }
}
