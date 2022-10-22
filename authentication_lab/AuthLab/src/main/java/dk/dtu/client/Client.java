package dk.dtu.client;

import dk.dtu.server.HelloService;
import dk.dtu.server.PrinterService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
	private static final String url = "rmi://localhost:5099";

	public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
		HelloService hello = (HelloService) Naming.lookup(url + "/hello");
		PrinterService printer = (PrinterService) Naming.lookup(url + "/printer");
//		System.out.println(hello.echo("hey server"));
		System.out.println(printer.start());
		System.out.println(printer.print("test1.txt", "printer1"));
		System.out.println(printer.readConfig("fail"));
		System.out.println(printer.setConfig("printers", "2"));
		System.out.println(printer.readConfig("printers"));
	}
}
