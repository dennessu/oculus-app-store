package CustomerScenarios.util;

/**
 * Created by Jason on 2/27/14.
 */
public class BaseTestClass {

    public enum PasswordStrength {
        WEAK,
        STRONG
    }

    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        BANNED,
        DELETED
    }

    public enum UserPara {
        userName,
        password,
        passwordStrength,
        status
    }

}
