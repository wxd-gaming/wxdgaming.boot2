package code.asm;

public class LoginHandler {

    public void login(Integer username, String password) {
        System.out.println("LoginHandler.login() %s %s".formatted(username, password));
    }

    public void login2(boolean append, byte code, int username, Integer userchannel, String password) {
        System.out.println(
                "LoginHandler.login2() append=%s code=%s username=%s userchannel=%s password=%s"
                        .formatted(append, code, username, userchannel, password)
        );
    }

}
