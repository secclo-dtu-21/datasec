package dk.dtu.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dk.dtu.server.entity.Printer;
import dk.dtu.server.entity.Session;
import dk.dtu.util.configuration.Configuration;
import dk.dtu.util.repository.AuthRepository;
import dk.dtu.util.repository.CryptoWrapper;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {
	private static Configuration conf;

	static {
		try {
			conf = Configuration.getInstance();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private static final Logger logger = LogManager.getLogger(PrinterServant.class);
	private List<Printer> printers;
	private HashMap<String, String> configs;
	private boolean isStart = false;
	private Map<String, Session> sessions;
	private Map<String, String> sessionUsernames;
	private AuthRepository authRepository;

	protected PrinterServant() throws RemoteException {
		printers = new ArrayList<>();
		printers.add(new Printer("printer1"));
		printers.add(new Printer("printer2"));
		configs = new HashMap<>();
		sessions = new HashMap<>();
		sessionUsernames = new HashMap<>();
		authRepository = new AuthRepository();
	}

	@Override
	public String print(String filename, String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "print"))
			return "Please authenticate first";
		if (!isStart)
			return "The printing service is not started";

		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.addFile(filename);
				return "The print task has been added to " + printer;
			}
		}

		return "Can not find the specified printer";
	}

	@Override
	public String queue(String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "queue"))
			return "Please authenticate first";
		if (!isStart)
			return "The printing service is not started";

		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.listQueue();
				return "Details are shown in log";
			}
		}
		return "Can not find the specified printer";
	}

	@Override
	public String topQueue(String printer, int job, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "topQueue"))
			return "Please authenticate first";
		if (!isStart)
			return "The print server is not started";

		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.topQueue(job);
				return "The specified job is moved";
			}
		}
		return "Can not find the specified printer";
	}

	@Override
	public String start(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "start"))
			return "Please authenticate first";
		if (isStart) {
			return "The printing service is already started";
		}
		isStart = true;
		return "The printing service is started";
	}

	@Override
	public String stop(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "stop"))
			return "Please authenticate first";
		if (!isStart) {
			return "The printing service is not started";
		}
		isStart = false;
		sessions.remove(cookie);
		sessionUsernames.remove(cookie);
		return "Service stop";
	}

	@Override
	public String restart(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "restart"))
			return "Please authenticate first";
		if (!isStart) {
			return "The printing service is not started";
		}

		for (var p : printers) {
			p.clearQueue();
		}
		sessions.remove(cookie);
		sessionUsernames.remove(cookie);
		return "The printing service is restarted";
	}

	@Override
	public String status(String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "status"))
			return "Please authenticate first";
		if (!isStart) {
			return "The specified print server is not started";
		}
		for (var p : printers) {
			if (p.getName().equals(printer)) {
				int status = p.getStatus();
				return printer + " has " + status + " tasks.";
			}
		}
		return "Can not find the specified printer";
	}

	@Override
	public String readConfig(String parameter, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "readConfig"))
			return "Please authenticate first";
		if (!isStart) {
			return "The printing service is not started";
		}
		if (configs.containsKey(parameter)) {
			logger.info(String.format("The value of the %s configuration is %s.", parameter, configs.get(parameter)));
			return String.format("The value of the parameter \'%s\' is written to log", parameter);
		}

		String error = String.format("There is no \'%s\' configuration on the server", parameter);
		logger.error(error);
		return error;
	}

	@Override
	public String setConfig(String parameter, String value, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "setConfig"))
			return "Please authenticate first";
		if (!isStart) {
			return "The printing service is not started";
		}
		configs.put(parameter, value);

		String success = String.format("The \'%s\' configuration has been set to \'%s\'", parameter, value);
		logger.info(success);
		return success;
	}

	@Override
	public String authenticate(String username, String password, int validSessionTime) throws RemoteException {
		// get value from db
		String passwordHash = authRepository.getUserPasswordHashByName(username);

		if (passwordHash == null) // Done so that the response always takes the same time
			passwordHash = conf.getRandomHash(); // random value

		if (!CryptoWrapper.checkAuthKey(password, passwordHash)) {
			logger.info(String.format("Failed authentication request for username: \'%s\'", username));
			return "Authentication fail: invalid username or password";
		}
		// If authentication success, return an uuid as cookie and add the associated
		// session
		String uuid = (UUID.randomUUID()).toString();
		Session session = new Session(validSessionTime);
		sessions.put(uuid, session);
		sessionUsernames.put(uuid, username);

		logger.info(String.format("User \'%s\' authenticate successfully", username));
		return uuid;
	}

	private boolean isAuthenticated(String cookie, String serviceName) {
		if (!sessions.containsKey(cookie))
			return false;
		Session session = sessions.get(cookie);
		if (!session.isAuthenticated()) {
			sessions.remove(cookie);
			sessionUsernames.remove(cookie);
			return false;
		}
		logger.info(String.format("User \'%s\' is requesting the use of service \'%s\'", sessionUsernames.get(cookie),
				serviceName));
		return true;
	}

}
