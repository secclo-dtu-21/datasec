package dk.dtu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {
    private static final Logger logger = LogManager.getLogger(PrinterServant.class);

    protected PrinterServant() throws RemoteException {
    }


    @Override
    public String print(String filename, String printer) throws RemoteException {
        return null;
    }

    @Override
    public String queue(String printer) throws RemoteException {
        return null;
    }

    @Override
    public String topQueue(String printer, int job) throws RemoteException {
        return null;
    }

    @Override
    public String start() throws RemoteException {
        logger.info("Info log message");

        return "test";
    }

    @Override
    public String stop() throws RemoteException {
        return null;
    }

    @Override
    public String restart() throws RemoteException {
        return null;
    }

    @Override
    public String status(String printer) throws RemoteException {
        return null;
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        return null;
    }

    @Override
    public String setConfig(String parameter, String value) throws RemoteException {
        return null;
    }
}
