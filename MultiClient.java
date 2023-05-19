package com.filetransfer;

import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

// For Calculating Average Search Response time
public class MultiClient {
public static void main(String [] args) throws IOException{
		
		String single = null;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the choice for Single Client Evaluation or Multi Client (5 Clients) : ");
		single = sc.nextLine();

		if(single.equalsIgnoreCase("Single")){
			String clientPortno1="4001";
			System.out.println("Please Enter a path");
			String directoryName1 = sc.nextLine();
			try{ 
				Registry registry = LocateRegistry.createRegistry(Integer.parseInt(clientPortno1));
				AvgRespFileSearch c=new AvgRespFileSearch(clientPortno1,directoryName1,"Peer1");
				PeerDownloadInterface pdInter = (PeerDownloadInterface) UnicastRemoteObject.exportObject(c,0);
				registry.rebind("root://PeerTest/"+clientPortno1+"/FS", pdInter);
				c.doWork();
			}
			catch(Exception e) {
		         System.err.println("FileServer exception: "+ e.getMessage());
		         e.printStackTrace();
		   }
		}
		// Running 5 Peers at once for performing the load testing.
		else if(single.equalsIgnoreCase("Multi")){
			String clientPortno1="1001";
		    String clientPortno2="1002";
		    String clientPortno3="1003";
		    String clientPortno4="1004";
		    String clientPortno5="1005";

			System.out.println("Please Enter a path for 1st Peer");
			String directoryName1 = sc.nextLine();
			System.out.println("Please Enter a path for 2nd Peer");
			String directoryName2 = sc.nextLine();
			System.out.println("Please Enter a path for 3rd Peer");
			String directoryName3 = sc.nextLine();
			System.out.println("Please Enter a path for 4th Peer");
			String directoryName4 = sc.nextLine();
			System.out.println("Please Enter a path for 5th Peer");
			String directoryName5 = sc.nextLine();

		    try{	       
		    	 Registry registry1 = LocateRegistry.createRegistry(Integer.parseInt(clientPortno1));
		    	 registry1 = LocateRegistry.createRegistry(Integer.parseInt(clientPortno2));
		    	 registry1 = LocateRegistry.createRegistry(Integer.parseInt(clientPortno3));
		    	 registry1 = LocateRegistry.createRegistry(Integer.parseInt(clientPortno4));
		    	 registry1 = LocateRegistry.createRegistry(Integer.parseInt(clientPortno5));
		    	 
		    	 AvgRespFileSearch c1=new AvgRespFileSearch(clientPortno1,directoryName1,"Peer1");
		    	 AvgRespFileSearch c2=new AvgRespFileSearch(clientPortno2,directoryName2,"Peer2");
		    	 AvgRespFileSearch c3=new AvgRespFileSearch(clientPortno3,directoryName3,"Peer3");
		         AvgRespFileSearch c4=new AvgRespFileSearch(clientPortno4,directoryName4,"Peer4");
		         AvgRespFileSearch c5=new AvgRespFileSearch(clientPortno5,directoryName5,"Peer5");
		         
		         PeerDownloadInterface pdInter1 = (PeerDownloadInterface) UnicastRemoteObject.exportObject(c1,0);
		         PeerDownloadInterface pdInter2 = (PeerDownloadInterface) UnicastRemoteObject.exportObject(c2,0);
		         PeerDownloadInterface pdInter3 = (PeerDownloadInterface) UnicastRemoteObject.exportObject(c3,0);
		         PeerDownloadInterface pdInter4 = (PeerDownloadInterface) UnicastRemoteObject.exportObject(c4,0);
		         PeerDownloadInterface pdInter5 = (PeerDownloadInterface) UnicastRemoteObject.exportObject(c5,0);

				 registry1.rebind("root://PeerTest/"+clientPortno1+"/FS", pdInter1);
				 registry1.rebind("root://PeerTest/"+clientPortno2+"/FS", pdInter2);
				 registry1.rebind("root://PeerTest/"+clientPortno3+"/FS", pdInter3);
				 registry1.rebind("root://PeerTest/"+clientPortno3+"/FS", pdInter4);
				 registry1.rebind("root://PeerTest/"+clientPortno3+"/FS", pdInter5);

				 Thread thread1 = new Thread (c1); 
			     Thread thread2 = new Thread (c2);  
			     Thread thread3 = new Thread (c3);  
			     Thread thread4 = new Thread (c4);  
			     Thread thread5 = new Thread (c5);  
			     thread1.start();
			     thread2.start();
			     thread3.start();
			     thread4.start();
			     thread5.start();
			     
		 } catch(Exception e) {
		         System.err.println("FileServer exception: "+ e.getMessage());
		         e.printStackTrace();
		      }
		}
	}
}
