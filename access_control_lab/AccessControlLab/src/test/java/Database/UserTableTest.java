package Database;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.AuthRepository;
import dk.dtu.util.repository.CryptoWrapper;

/**
 * Unit testing of the dao function
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTableTest {

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
    private static AuthRepository authRepository = new AuthRepository();

    @Test
    @Order(1)
    public void addUserTest() {
        int result = authRepository.addUser(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPassword));
        assertThat(result).isEqualTo(1);
    }

    @Test
    @Order(2)
    public void getUserPasswordHashByNameTest() {
        String result = authRepository.getUserPasswordHashByName(testUsername);
        System.out.println(result);
        assertThat(result).isNotNull();
    }

    @Test
    @Order(3)
    public void clearDatabaseTest() {
        authRepository.clearDatabase();
        int number = authRepository.getDataNumber();
        assertThat(number).isEqualTo(0);
    }

}
