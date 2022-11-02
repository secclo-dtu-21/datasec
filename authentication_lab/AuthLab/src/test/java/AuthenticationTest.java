import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.AuthRepository;
import dk.dtu.util.repository.CryptoWrapper;

/**
 * Integration test of the printer system authentication
 */
public class AuthenticationTest {
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
	private int validSessionTime = conf.getValidSessionTime();
	private static AuthRepository authRepository = new AuthRepository();
	private static final String url = conf.getServiceUrl();
	static PrinterService printer;

	@BeforeAll
	public static void init() throws MalformedURLException, NotBoundException, RemoteException {

		authRepository.addUser(testUsername, CryptoWrapper.hashUserPwPBKDF(testUserPassword));
		printer = (PrinterService) Naming.lookup(url + "/printer");
	}

	@AfterAll
	public static void clean() {
		authRepository.clearDatabase();
	}

	@Test
	public void startThePrinter_whenWithoutAuthentication_receiveFailure() throws RemoteException {
		String response = printer.start("test");
		assertThat(response).isEqualTo("Please authenticate first");
	}

	@Test
	public void authenticate_withInvalidCredentials_receiveFailure() throws RemoteException {
		String response = printer.authenticate(testUsername, "wrongPassword", validSessionTime);
		assertThat(response).isEqualTo("Authentication fail: invalid username or password");
	}

	@Test
	public void authenticate_withInvalidUsername_receiveFailure() throws RemoteException {
		String response = printer.authenticate(testUsername + "fake", testUserPassword, validSessionTime);
		assertThat(response).isEqualTo("Authentication fail: invalid username or password");
	}

	@Test
	public void authenticate_withValidUsername_receiveSuccess() throws RemoteException {
		String response = printer.authenticate(testUsername, testUserPassword, validSessionTime);
		assertThat(response.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")).isTrue();
	}

	@Test
	public void authenticate_withSession_receiveSuccess() throws RemoteException {
		String cookie = printer.authenticate(testUsername, testUserPassword, validSessionTime);
		String result = printer.start(cookie);
		assertThat(result).isEqualTo("The printing service is started");
	}

	@Test
	public void authenticate_withOutDateSession_receivewFailure() throws RemoteException, InterruptedException {
		String cookie = printer.authenticate(testUsername, testUserPassword, validSessionTime);
		// Wait for a while until the session expired
		Thread.sleep(validSessionTime * 1000L);
		String result = printer.start(cookie);
		assertThat(result).isEqualTo("Please authenticate first");
	}
}
