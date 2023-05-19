package com.filetransfer;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;


//   This contains the main method for setting up the Peer.
public interface Peer{
	public static void main(String[] args) throws RemoteException {
		Scanner sc = new Scanner(System.in); 
		String portno=null;
	     System.out.println("Enter the port number on which peer needs to be registered");   
	     portno=sc.nextLine();
	     
	     System.out.println("Enter the directory path");
	     String directoryName = sc.nextLine();
		
	     // Registering the peer on specified port & setting up the remote object
    	 Registry registry = LocateRegistry.createRegistry(Integer.parseInt(portno));
    	 ClientInterface ciImpl = new ClientInterface(portno, directoryName);
    	 PeerDownloadInterface pdInter = (PeerDownloadInterface)UnicastRemoteObject.exportObject(ciImpl, 0);
		 registry.rebind("root://PeerTest/"+portno+"/FS", pdInter);
		 System.out.println("Peer is up and Running.");
		 try {
			ciImpl.doWork();
		} catch (IOException e) {
			System.out.println("IO Exception at Peer Main" + e.getMessage());
			e.printStackTrace();
		} 
}		
}
