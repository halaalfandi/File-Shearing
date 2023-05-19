package com.filetransfer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

// Creating Remote Interface of Index Server
public interface IndexServerInterface extends Remote {
	
	public void registryFiles(String rd,String filename, String peerid, String port_num, String directory) throws RemoteException;
	public Collection<ArrayList<String>> searchFile(String filename) throws RemoteException; 	
}
