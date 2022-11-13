package AccessControl;

import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.AuthRepository;
import dk.dtu.util.repository.CryptoWrapper;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class contains the test for the access control list mechanism. Before running tests in this class
 * make sure that the access control model has been configured to accessControlList. Namely, in this format:
 * accessControlModel = accessControlList
 */
public class AccessControlListTest {

    private static Configuration conf;

    static {
        try {
            conf = Configuration.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String url = conf.getServiceUrl();
    static PrinterService printer;
    private static String testUserPlaintextPassword = conf.getTestUserPassword();
    private static AuthRepository authRepository = new AuthRepository();
    private int validSessionTime = conf.getValidSessionTime();
    private static String testUsername;

    @BeforeAll
    public static void init() throws MalformedURLException, NotBoundException, RemoteException {
        printer = (PrinterService) Naming.lookup(url + "/printer");
    }

    @AfterEach
    public void clean() {
        authRepository.clearDatabase();
    }

    @Test
    @Order(1)
    public void testWithUserNameAlice() throws RemoteException {
        testUsername = "Alice";
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        authRepository.addUser(testUsername, CryptoWrapper.hashSaltAuthKey(testUserHashedPw));

        String cookie = printer.authenticate(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword), validSessionTime);
        assertThat(cookie.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")).isTrue();
        /* start allowed*/
        String response = printer.start(cookie);
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
        /* stop allowed*/
        response = printer.stop(cookie);
        assertThat(response).isEqualTo("Service stop");
    }

    @Test
    @Order(2)
    public void testWithUsernameBob() throws RemoteException {
        testUsername = "Bob";
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        authRepository.addUser(testUsername, CryptoWrapper.hashSaltAuthKey(testUserHashedPw));

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
    @Order(3)
    public void testWithUsernameCecilia() throws RemoteException {
        testUsername = "Cecilia";
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        authRepository.addUser(testUsername, CryptoWrapper.hashSaltAuthKey(testUserHashedPw));

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
    @Order(4)
    public void testWithUsernameDavid() throws RemoteException {
        testUsername = "David";
        String testUserHashedPw = CryptoWrapper.hashUserPwPBKDF(testUserPlaintextPassword);
        authRepository.addUser(testUsername, CryptoWrapper.hashSaltAuthKey(testUserHashedPw));

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

}
