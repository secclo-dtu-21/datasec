package Database;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.UserRepository;
import dk.dtu.util.cryto.CryptoWrapper;

/**
 * Unit testing of the dao function
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

    private static Configuration conf;

    static {
        try {
            conf = Configuration.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String testUsername = conf.getTestUsername();
    private static String testUserPassword = conf.getTestUserPassword();
    private static UserRepository userRepository = new UserRepository();

    @Test
    @Order(1)
    public void addUserTest() {
        int result = userRepository.addUser(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPassword), "none");
        assertThat(result).isEqualTo(1);
    }

    @Test
    @Order(2)
    public void getUserPasswordHashByNameTest() {
        String result = userRepository.getUserPasswordHashByName(testUsername);
        System.out.println(result);
        assertThat(result).isNotNull();
    }

    @Test
    @Order(3)
    public void clearDatabaseTest() {
        userRepository.clearDatabase();
        int number = userRepository.getDataNumber();
        assertThat(number).isEqualTo(0);
    }

}
