package CustomerScenarios.user;

import CustomerScenarios.helper.LogHelper;
import CustomerScenarios.util.BaseTestClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import CustomerScenarios.helper.AsyncHttpClientHelper;
import CustomerScenarios.helper.RandomFactory;

/**
 * Created by Jason on 2/25/14.
 */
public class TestPostUser extends BaseTestClass {

    private static LogHelper logger = new LogHelper(TestPostUser.class);

    private final String serverURL = "http://10.0.0.111:8081/rest/users";
    private final String weakPassword = "password";
    private final String strongPassword = "Welcome123";

    @Test(
            description = "Test Post User",
            enabled = true
    )
    public void testPostUser() throws Exception {

        AsyncHttpClientHelper clientHelper = new AsyncHttpClientHelper();
        StringBuilder postRequestBody = new StringBuilder();
        postRequestBody.append("{\"");
        postRequestBody.append(UserPara.userName.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(RandomFactory.getRandomEmailAddress());
        postRequestBody.append("\",\"");
        postRequestBody.append(UserPara.password.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(weakPassword);
        postRequestBody.append("\",\"");
        postRequestBody.append(UserPara.passwordStrength.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(PasswordStrength.WEAK.toString());
        postRequestBody.append("\",\"");
        postRequestBody.append(UserPara.status.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(UserStatus.ACTIVE.toString());
        postRequestBody.append("\"}");

        String response = clientHelper.UserPost(serverURL,
                postRequestBody.toString());
        System.out.println("The Response Body is: " + response);
        String[] results = response.split(",");
        String entityId = null;
        for (String s : results) {
            if (s.contains("id")) {
                entityId = s.replace("{\"id\":\"", "").replace("\"", "").trim();
            }
        }

        Assert.assertNotNull(entityId);
    }
}