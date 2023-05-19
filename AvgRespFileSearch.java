package com.filetransfer;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class AvgRespFileSearch implements Runnable, PeerDownloadInterface{
	String portNo = null;
	String dirName = null;
	String peerID = null;
	String fileName = "Process Flow.txt";
	Collection<ArrayList<String>> colArr;
	long start = 0;
	long end = 0;
	long responseTime = 0;
	int seqReq=500;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		doWork();
	}	
	public void doWork() {	
		try{
			// Locating Registry of Indexing Server and obtains target address 
			Registry regis = LocateRegistry.getRegistry("localhost", 3455);
			IndexServerInterface isInter = (IndexServerInterface) regis.lookup("Indexing");
	
			Scanner sc = new Scanner(System.in);

			File dirList = new File(dirName);
			//List of all files in the directory
			String[] record = dirList.list();
			
			// Registering Files in Index Server
			for(int c=0; c < record.length; c++){
				File currentFile = new File(record[c]);
				System.out.println("Registering details of File name " + currentFile.getName() + " in Indexing Server");
				isInter.registryFiles("new",currentFile.getName(), peerID, portNo, dirName);
			}
			// Running Sequential 500 search requests to Index server
            start = System.nanoTime();
            System.out.println("start is: " + start);
            for (int i=0;i<seqReq;i++){
            	colArr = isInter.searchFile(fileName);
            }
            end= System.nanoTime();
            System.out.println("end is: " + end);
            responseTime= (end - start)/1000000; // Milliseconds
            //System.out.println("ResponseTime for PeerID " +peerID +" is "+ responseTime + " ms");
            float avgRespTime = (float)responseTime/seqReq;
            System.out.println("Avg Response time of PeerID " +peerID +" is "+avgRespTime + "ms");
            
			}catch(Exception e) {
				System.out.println("MultiClientFileSearch exception: " + e);
			}
	
	}
	@Override
	public byte[] fileDownload(ArrayList<String> searchedDir) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	// Initializing the variables with the help of constructors
	AvgRespFileSearch (String portNo, String dirName, String peerId){
		this.portNo = portNo;
		this.dirName = dirName;
		this.peerID = peerId;
	}

	
}