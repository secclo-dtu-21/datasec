package dk.dtu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import dk.dtu.server.entity.Printer;
import dk.dtu.server.entity.Session;
import dk.dtu.util.repository.AuthRepository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.mindrot.jbcrypt.BCrypt;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {
	private static final Logger logger = LogManager.getLogger(PrinterServant.class);
	private List<Printer> printers;
	private HashMap<String, String> configs;
	private boolean isStart = false;
	private Map<String, Session> sessions;
	private AuthRepository authRepository;

	protected PrinterServant() throws RemoteException {
		printers = new ArrayList<>();
		printers.add(new Printer("printer1"));
		printers.add(new Printer("printer2"));
		configs = new HashMap<>();
		sessions = new HashMap<>();
		authRepository = new AuthRepository();
	}

	@Override
	public String print(String filename, String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie))
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
		if (!isAuthenticated(cookie))
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
		if (!isAuthenticated(cookie))
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
		if (!isAuthenticated(cookie))
			return "Please authenticate first";
		if (isStart) {
			return "The printing service is already started";
		}
		isStart = true;
		return "The printing service is started";
	}

	@Override
	public String stop(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie))
			return "Please authenticate first";
		if (!isStart) {
			return "The printing service is not started";
		}
		isStart = false;
		sessions.remove(cookie);
		return "Service stop";
	}

	@Override
	public String restart(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie))
			return "Please authenticate first";
		if (!isStart) {
			return "The printing service is not started";
		}

		for (var p : printers) {
			p.clearQueue();
		}
		sessions.remove(cookie);
		return "The printing service is restarted";
	}

	@Override
	public String status(String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie))
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
		if (!isAuthenticated(cookie))
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
		if (!isAuthenticated(cookie))
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
		String passwordHash = authRepository.getUserPasswordHashByName(username);
		if (passwordHash == null)
			return "Invalid username";
		if (!BCrypt.checkpw(password, passwordHash))
			return "Authentication fail";
		// If authentication success, return an uuid as cookie and add the associated session
		String uuid = (UUID.randomUUID()).toString();
		Session session = new Session(validSessionTime);
		sessions.put(uuid, session);

		logger.info(String.format("User \'%s\' authenticate successfully", username));
		return uuid;
	}

	private boolean isAuthenticated(String cookie) {
		if (!sessions.containsKey(cookie))
			return false;
		Session session = sessions.get(cookie);
		return session.isAuthenticated();
	}

}
