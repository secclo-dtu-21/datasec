package dk.dtu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import dk.dtu.server.entity.Printer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {
    private static final Logger logger = LogManager.getLogger(PrinterServant.class);
    private List<Printer> printers;

    protected PrinterServant() throws RemoteException {
        printers = new ArrayList<>();
        printers.add(new Printer("printer1"));
        printers.add(new Printer("printer2"));
    }


    @Override
    public String print(String filename, String printer) throws RemoteException {
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
        return "";
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
