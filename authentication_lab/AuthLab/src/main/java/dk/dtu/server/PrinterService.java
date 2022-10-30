package dk.dtu.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterService extends Remote {

    public String print(String filename, String printer, String cookie) throws RemoteException;

    public String queue(String printer, String cookie) throws RemoteException;

    public String topQueue(String printer, int job, String cookie) throws RemoteException;

    public String start(String cookie) throws RemoteException;

    public String stop(String cookie) throws RemoteException;

    public String restart(String cookie) throws RemoteException;

    public String status(String printer, String cookie) throws RemoteException;

    public String readConfig(String parameter, String cookie) throws RemoteException;

    public String setConfig(String parameter, String value, String cookie) throws RemoteException;

    String authenticate(String username, String password, int validTime) throws RemoteException;
}
