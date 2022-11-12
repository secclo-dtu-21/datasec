package dk.dtu.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import dk.dtu.util.configuration.Configuration;

public class ApplicationServer {

	private static Configuration conf;

	static {
		try {
			conf = Configuration.getInstance();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws RemoteException {
		Registry registry = LocateRegistry.createRegistry(conf.getServicePort());
		registry.rebind("printer", new PrinterServant());
	}
}
