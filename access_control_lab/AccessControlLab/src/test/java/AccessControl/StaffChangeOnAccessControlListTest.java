package AccessControl;

import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.cryto.CryptoWrapper;
import dk.dtu.util.repository.AccessControlRepository;
import dk.dtu.util.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test corresponding to the final task, which implement the necessary changes in access control lists to reflect
 * organisational changes in the company. To run the test, the access policy should be configured as follows:
 * accessControlModel = accessControlList
 */
public class StaffChangeOnAccessControlListTest {

    private static final Configuration conf;

    static {
        try {
            conf = Configuration.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String url = conf.getServiceUrl();
    static PrinterService printer;
    private static final String testUserPlaintextPassword = conf.getTestUserPassword();
    private static final UserRepository userRepository = new UserRepository();
    private static final AccessControlRepository accessControlRepository = new AccessControlRepository();
    private final int validSessionTime = conf.getValidSessionTime();

    @BeforeAll
    public static void init() throws MalformedURLException, NotBoundException, RemoteException {
        printer = (PrinterService) Naming.lookup(url + "/printer");
    }

    @AfterAll
    public static void clean() {
        userRepository.clearDatabase();
        accessControlRepository.clearDatabase();
    }

    @Test
    public void testWithStaffChange_withAccessControlList_shouldBeSuccess() throws RemoteException {
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        /**
         * Add all the initial users and related control policy, notice, the role fields are all set to none
         * since here we are using access control list policy rather than role based control policy
         */
        userRepository.addUser("Alice", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("Alice", "111111111");
        userRepository.addUser("Bob", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("Bob", "000111111");
        userRepository.addUser("Cecilia", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("Cecilia", "111001000");
        userRepository.addUser("David", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("David", "110000000");
        userRepository.addUser("Erica", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("Erica", "110000000");
        userRepository.addUser("Fred", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("Fred", "110000000");
        userRepository.addUser("George", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        accessControlRepository.addAccessControlList("George", "110000000");

        /* Delete user Bob */
        userRepository.deleteUserByName("Bob");
        accessControlRepository.deleteAccessControlListByName("Bob");
        /* Change the permissions of George */
        accessControlRepository.updateAccessControlList("George", "110000111");
        /* Add Henry */
        accessControlRepository.addAccessControlList("Henry", "110000000");
        userRepository.addUser("Henry", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");
        /* Add Ida */
        accessControlRepository.addAccessControlList("Ida", "111001000");
        userRepository.addUser("Ida", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "none");

        String cookie = printer.authenticate("Alice", CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        String response = printer.start(cookie);
        assertThat(response).isEqualTo("The printing service is started");

        /* Test the permissions of George */
        cookie = printer.authenticate("George", CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        response = printer.print("file_George", "printer1", cookie);
        assertThat(response.contains("task has been added")).isTrue();
        response = printer.status("printer1", cookie);
        assertThat(response.contains("tasks")).isTrue();

        /* Test the permissions of Henry */
        cookie = printer.authenticate("Henry", CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        response = printer.print("file_Henry", "printer1", cookie);
        assertThat(response.contains("task has been added")).isTrue();
        response = printer.stop(cookie);
        assertThat(response.contains("not allowed")).isTrue();

        /* Test the permissions of Ida */
        cookie = printer.authenticate("Ida", CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        response = printer.print("file_Ida", "printer1", cookie);
        assertThat(response.contains("task has been added")).isTrue();
        response = printer.restart(cookie);
        assertThat(response).isEqualTo("The printing service is restarted");
    }

}
