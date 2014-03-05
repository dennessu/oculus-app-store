package CustomerScenarios.helper;

import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Jason on 2/26/14.
 */
public class RandomFactory {

    private static final String nameCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String TEST_NAMESPACE_NAME = "customerscenarios";
    private static final Random random = new Random();

    public static String getRandomEmailAddress() {
        int rand1 = random.nextInt(100000);
        int rand2 = random.nextInt(100000);
        String email = rand1 + TEST_NAMESPACE_NAME + GregorianCalendar.getInstance().getTimeInMillis() + rand2 + "@ecommerce.com";
        return email;
    }

}
