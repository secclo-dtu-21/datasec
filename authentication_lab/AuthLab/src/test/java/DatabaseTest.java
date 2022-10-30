import dk.dtu.server.PrinterService;
import dk.dtu.util.repository.AuthRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mindrot.jbcrypt.BCrypt;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit testing of the dao function
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTest {

    private static String testUsername = "user1";
    private static String testUserPassword = "passw0rd";
    private int validSessionTime = 10;
    private static AuthRepository authRepository = new AuthRepository();

    @Test
    @Order(1)
    public void addUserTest() {
        int result = authRepository.addUser(testUsername, BCrypt.hashpw(testUserPassword, BCrypt.gensalt()));
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
