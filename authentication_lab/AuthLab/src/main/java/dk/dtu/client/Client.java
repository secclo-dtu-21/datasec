package dk.dtu.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;
import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.AuthRepository;

public class Client {

	private static final Configuration conf;

	static {
		try {
			conf = Configuration.getInstance();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private static final String url = conf.getServiceUrl();
	private static final String testUsername = conf.getTestUsername();
	private static final String testUserPassword = conf.getTestUserPassword();
	private static final AuthRepository authRepository = new AuthRepository();
	private static final int validSessionTime = conf.getValidSessionTime();

	public static void main(String[] args)
			throws MalformedURLException, NotBoundException, RemoteException, NoSuchAlgorithmException {

		Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
		// OWASP recommendation:
		// iteration count = 2, memory = 15 MiB, degree of parallelism = 1
		String pwHash = argon2.hash(2, 15 * 1024, 1, testUserPassword.toCharArray());

		// Add test user to database
		authRepository.addUser(testUsername, pwHash);

		// The following codes describe a typical procedure to use the printing service
		PrinterService printer = (PrinterService) Naming.lookup(url + "/printer");
		String cookie = printer.authenticate(testUsername, testUserPassword, validSessionTime);
		System.out.println(printer.start(cookie));
		System.out.println(printer.print("test1.txt", "printer1", cookie));
		System.out.println(printer.print("test2.txt", "printer1", cookie));
		System.out.println(printer.print("test3.txt", "printer1", cookie));
		System.out.println(printer.print("test4.txt", "printer2", cookie));
		System.out.println(printer.print("test5.txt", "printer1", cookie));
		System.out.println(printer.queue("printer1", cookie));
		System.out.println(printer.topQueue("printer1", 3, cookie));
		System.out.println(printer.queue("printer1", cookie));
		System.out.println(printer.status("printer1", cookie));
		System.out.println(printer.restart(cookie));
		cookie = printer.authenticate(testUsername, testUserPassword, validSessionTime);
		System.out.println(printer.queue("printer1", cookie));
		System.out.println(printer.queue("printer2", cookie));
		System.out.println(printer.setConfig("printers", "2", cookie));
		System.out.println(printer.readConfig("printers", cookie));
		System.out.println(printer.stop(cookie));

		// Clear the database
		authRepository.clearDatabase();
	}

}
