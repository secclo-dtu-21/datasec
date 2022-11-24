package AccessControl;

import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.cryto.CryptoWrapper;
import dk.dtu.util.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test corresponding to the final task, which implement the necessary changes in role based access control policy to
 * reflect organisational changes in the company. To run the test, the access policy should be configured as follows:
 * accessControlModel = roleBasedAccessControl
 */
public class StaffChangeOnRoleBasedControlTest {

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
    private final int validSessionTime = conf.getValidSessionTime();

    @BeforeAll
    public static void init() throws MalformedURLException, NotBoundException, RemoteException {
        printer = (PrinterService) Naming.lookup(url + "/printer");
    }

    @AfterAll
    public static void clean() {
        userRepository.clearDatabase();
    }

    @Test
    public void testWithStaffChange_withRoleBasedControl_shouldBeSuccess() throws RemoteException {
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        /* Add all the initial users */
        List<String> acTestUsers = conf.getACTestUsers();
        List<String> userRoles = conf.getUserRoles();
        for (int i = 0; i < acTestUsers.size(); i++) {
            userRepository.addUser(acTestUsers.get(i), CryptoWrapper.hashSaltAuthKey(testUserHashedPw), userRoles.get(i));
        }


        /* Delete user Bob */
        userRepository.deleteUserByName("Bob");
        /* Change the role of George */
        String originalRole = userRepository.getUserRoleByName("George");
        userRepository.updateUserRoleByName(originalRole + "&" + "service_tech", "George");
        /* Add new employee Henry */
        userRepository.addUser("Henry", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "ordinary_user");
        /* Add new employee Ida */
        userRepository.addUser("Ida", CryptoWrapper.hashSaltAuthKey(testUserHashedPw), "power_user");

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
