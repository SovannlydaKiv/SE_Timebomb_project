import model.User;

public class UserSession {
    private static User currentUser = null;
    private static String username = "";
    private static String email = "";
    private static String password = "";

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user != null) {
            username = user.getUsername();
            email = user.getEmail();
            password = user.getPassword();
        }
    }

    public static Long getUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public static String getFullName() {
        return currentUser != null ? currentUser.getFullName() : "";
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPassword() {
        return password;
    }

    public static void setUsername(String u) {
        if (u != null) username = u;
    }

    public static void setEmail(String e) {
        if (e != null) email = e;
    }

    public static void setPassword(String p) {
        if (p != null) password = p;
    }

    public static void set(String u, String e) {
        setUsername(u);
        setEmail(e);
    }

    public static void set(String u, String e, String p) {
        setUsername(u);
        setEmail(e);
        setPassword(p);
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
        username = "";
        email = "";
        password = "";
    }
}

