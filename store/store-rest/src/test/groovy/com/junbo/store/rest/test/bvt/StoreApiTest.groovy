package com.junbo.store.rest.test.bvt
import com.junbo.store.rest.test.Generator
import com.junbo.store.spec.model.identity.UserProfileGetRequest
import com.junbo.store.spec.resource.LoginResource
import com.junbo.store.spec.resource.StoreResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
/**
 * The StoreApiTest class.
 */
@Test
@ContextConfiguration(locations = ['classpath:spring/store-rest-test-context.xml'])
class StoreApiTest extends AbstractTestNGSpringContextTests {

    @Autowired(required = true)
    @Qualifier('storeResourceClientProxy')
    private StoreResource storeResource

    @Autowired(required = true)
    @Qualifier('loginResourceClientProxy')
    private LoginResource loginResource

    public void testUserProfile() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        def userId = result.userId

        result = storeResource.getUserProfile(new UserProfileGetRequest(userId: userId)).get()
        assert result.userProfile.userId == userId
        assert result.userProfile.emails.size() == 1
        assert result.userProfile.emails[0].type == 'EMAIL'

        //storeResource.updateUserProfile(new UserProfileUpzdateRequest())

    }


}
