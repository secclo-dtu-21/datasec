package dk.dtu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dtu.server.entity.Printer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {
	private static final Logger logger = LogManager.getLogger(PrinterServant.class);
	private List<Printer> printers;
	private HashMap<String, String> configs;
	private boolean isStart = false;

	protected PrinterServant() throws RemoteException {
		printers = new ArrayList<>();
		printers.add(new Printer("printer1"));
		printers.add(new Printer("printer2"));
		configs = new HashMap<>();
	}

	@Override
	public String print(String filename, String printer) throws RemoteException {
		if (!isStart) {
			return "The print server is not started";
		}
		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.addFile(filename);
				return "Success";
			}
		}
		return "Can not find the specified printer";
	}

	@Override
	public String queue(String printer) throws RemoteException {
		if (!isStart) {
			return "The print server is not started";
		}
		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.listQueue();
				return "Success";
			}
		}
		return "Can not find the specified printer";
	}

	@Override
	public String topQueue(String printer, int job) throws RemoteException {
		if (!isStart) {
			return "The print server is not started";
		}
		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.topQueue(job);
				return "Success";
			}
		}
		return "Can not find the specified printer";
	}

	@Override
	public String start() throws RemoteException {
		if (isStart) {
			return "The printer server is already started";
		}
		isStart = true;
		return "Success";
	}

	@Override
	public String stop() throws RemoteException {
		if (!isStart) {
			return "The print server is not started";
		}
		isStart = false;
		return "Success";
	}

	@Override
	public String restart() throws RemoteException {
		if (!isStart) {
			return "The print server is not started";
		}
		isStart = false;
		for (var p : printers) {
			p.clearQueue();
		}
		return "Success";
	}

	@Override
	public String status(String printer) throws RemoteException {
		if (!isStart) {
			return "The print server is not started";
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
	public String readConfig(String parameter) throws RemoteException {
		if (configs.containsKey(parameter)) {
			logger.info(String.format("The value of the %s configuration is %s.", parameter, configs.get(parameter)));
			return parameter;
		}

		String error = String.format("There is no \'%s\' configuration on the server", parameter);
		logger.error(error);
		return error;
	}

	@Override
	public String setConfig(String parameter, String value) throws RemoteException {

		configs.put(parameter, value);

		String success = String.format("The \'%s\' configuration has been set to \'%s\'", parameter, value);
		logger.info(success);
		return success;
	}
}
