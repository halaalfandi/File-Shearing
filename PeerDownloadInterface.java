package com.filetransfer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

// Creating Remote Interface of Peer
public interface PeerDownloadInterface extends Remote {
	public byte[] fileDownload(ArrayList<String> searchedDir) throws RemoteException;
}