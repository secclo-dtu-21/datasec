package dk.dtu.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import dk.dtu.server.PrinterService;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.UserRepository;
import dk.dtu.util.cryto.CryptoWrapper;

/**
 * Typical client use flow is depicted in the test under the test/java/AccessControl package, including
 * registering user, user performing operations and cleaning user.
 */
public class Client {

	public static void main(String[] args)
			throws MalformedURLException, NotBoundException, RemoteException, NoSuchAlgorithmException {

	}
}
