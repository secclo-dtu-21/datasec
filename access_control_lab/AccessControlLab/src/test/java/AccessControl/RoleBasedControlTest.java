package AccessControl;

import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.cryto.CryptoWrapper;
import dk.dtu.util.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class contains the test for the role based control mechanism. Before running tests in this class
 * make sure that the access control model has been configured to roleBasedAccessControl. Namely, in this format:
 * accessControlModel = roleBasedAccessControl
 *
 * Note, these tests are arranged in certain order to make sure the consistency of the tests, they should not
 * be changed.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleBasedControlTest {

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
    private static String testUsername;

    @BeforeAll
    public static void init() throws MalformedURLException, NotBoundException, RemoteException {
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        List<String> acTestUsers = conf.getACTestUsers();
        List<String> userRoles = conf.getUserRoles();
        for (int i = 0; i < acTestUsers.size(); i++) {
            userRepository.addUser(acTestUsers.get(i), CryptoWrapper.hashSaltAuthKey(testUserHashedPw), userRoles.get(i));
        }

        printer = (PrinterService) Naming.lookup(url + "/printer");
    }

    @AfterAll
    public static void clean() {
        userRepository.clearDatabase();
    }

    @Test
    @Order(1)
    public void testWithUsernameBob() throws RemoteException {
        testUsername = "Bob";

        String cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        assertThat(cookie.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")).isTrue();
        /* start allowed*/
        String response = printer.start(cookie);
        assertThat(response).isEqualTo("The printing service is started");
        /* print allowed*/
        response = printer.print("test1_Bob", "printer1", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* queue rejected*/
        response = printer.queue("printer1", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* topQueue rejected*/
        printer.print("test2_Bob", "printer1", cookie);
        response = printer.topQueue("printer1", 2, cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* status allowed*/
        response = printer.status("printer1", cookie);
        assertThat(response.contains("tasks")).isTrue();
        /* restart allowed*/
        response = printer.restart(cookie);
        assertThat(response).isEqualTo("The printing service is restarted");

        cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);

        /* setConfig allowed*/
        response = printer.setConfig("paramBob", "Bob", cookie);
        assertThat(response.contains("configuration has been")).isTrue();
        /* readConfig allowed*/
        String param1 = printer.readConfig("paramBob", cookie);
        assertThat(response.contains("paramBob")).isTrue();
        /* stop allowed*/
        response = printer.stop(cookie);
        assertThat(response).isEqualTo("Service stop");

        cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        printer.start(cookie);
    }

    @Test
    @Order(2)
    public void testWithUsernameCecilia() throws RemoteException {
        testUsername = "Cecilia";

        String cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        assertThat(cookie.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")).isTrue();
        /* start rejected*/
        String response = printer.start(cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* print allowed*/
        response = printer.print("test1_Cecilia", "printer1", cookie);
        System.out.println(response);
        assertThat(response.contains("task has been added")).isTrue();
        /* queue allowed*/
        response = printer.queue("printer1", cookie);
        assertThat(response).isEqualTo("Details are shown in log");
        /* topQueue allowed*/
        printer.print("test2_Cecilia", "printer1", cookie);
        response = printer.topQueue("printer1", 2, cookie);
        assertThat(response).isEqualTo("The specified job is moved");
        /* status rejected*/
        response = printer.status("printer1", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* restart allowed*/
        response = printer.restart(cookie);
        assertThat(response).isEqualTo("The printing service is restarted");

        cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);

        /* setConfig rejected*/
        response = printer.setConfig("paramCecilia", "Cecilia", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* readConfig rejected*/
        String param1 = printer.readConfig("paramCecilia", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* stop rejected*/
        response = printer.stop(cookie);
        assertThat(response.contains("not allowed")).isTrue();
    }

    /**
     * User David, Erica, Fred and George have same permission, therefore, only David is tested here.
     * @throws RemoteException
     */
    @Test
    @Order(3)
    public void testWithUsernameDavid() throws RemoteException {
        testUsername = "David";

        String cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        assertThat(cookie.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")).isTrue();
        /* start rejected*/
        String response = printer.start(cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* print allowed*/
        response = printer.print("test1_Cecilia", "printer1", cookie);
        assertThat(response.contains("task has been added")).isTrue();
        /* queue allowed*/
        response = printer.queue("printer1", cookie);
        assertThat(response).isEqualTo("Details are shown in log");
        /* topQueue rejected*/
        printer.print("test2_Cecilia", "printer1", cookie);
        response = printer.topQueue("printer1", 2, cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* status rejected*/
        response = printer.status("printer1", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* restart rejected*/
        response = printer.restart(cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* setConfig rejected*/
        response = printer.setConfig("paramCecilia", "Cecilia", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* readConfig rejected*/
        String param1 = printer.readConfig("paramCecilia", cookie);
        assertThat(response.contains("not allowed")).isTrue();
        /* stop rejected*/
        response = printer.stop(cookie);
        assertThat(response.contains("not allowed")).isTrue();
    }

    @Test
    @Order(4)
    public void testWithUserNameAlice() throws RemoteException {
        testUsername = "Alice";

        String cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        /* stop allowed*/
        String  response = printer.stop(cookie);
        assertThat(response).isEqualTo("Service stop");

        cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);

        /* start allowed*/
        response = printer.start(cookie);
        assertThat(response).isEqualTo("The printing service is started");
        /* print allowed*/
        response = printer.print("test1_Alice", "printer1", cookie);
        assertThat(response.contains("task has been added")).isTrue();
        /* queue allowed*/
        response = printer.queue("printer1", cookie);
        assertThat(response).isEqualTo("Details are shown in log");
        /* topQueue allowed*/
        printer.print("test2_Alice", "printer1", cookie);
        response = printer.topQueue("printer1", 2, cookie);
        assertThat(response).isEqualTo("The specified job is moved");
        /* status allowed*/
        response = printer.status("printer1", cookie);
        assertThat(response.contains("tasks")).isTrue();
        /* restart allowed*/
        response = printer.restart(cookie);
        assertThat(response).isEqualTo("The printing service is restarted");

        cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);

        /* setConfig allowed*/
        response = printer.setConfig("paramAlice", "Alice", cookie);
        assertThat(response.contains("configuration has been")).isTrue();
        /* readConfig allowed*/
        response = printer.readConfig("paramAlice", cookie);
        assertThat(response.contains("paramAlice")).isTrue();

        printer.stop(cookie);
    }

}
