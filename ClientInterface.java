package com.filetransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.nio.file.Files;

public class ClientInterface implements PeerDownloadInterface{
	String portNo = null; // Port no. of the peer
	String dirName = null; //Directory where the files are to be stored.
	String fileName = null; //the file to be searched.
	String remotePeer= null; //Peer from whom file has to be downloaded.
	Collection<ArrayList<String>> colArr;

	ClientInterface (String portNo, String dirName){
		this.portNo = portNo;
		this.dirName = dirName;
	}
	// this method is used for Registering new entry, Searching , Deleting a file and Downloading a file.
	public void doWork() throws IOException {
		String peerID = null; //peerID
		try{
			// Locating Registry of Indexing Server and obtains target address 
			Registry regis = LocateRegistry.getRegistry("localhost", 3455);
			IndexServerInterface isInter = (IndexServerInterface) regis.lookup("Indexing");
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter Your Peer ID");
			peerID = sc.nextLine();
			
			//obtain directory name where file is located
			File dirList = new File(dirName);
			//list of all records in the directory
			String[] record = dirList.list();

			// Registering Files in Index Server
			for(int c=0; c < record.length; c++){
				File currentFile = new File(record[c]);
				System.out.println("Registering details of File name " + currentFile.getName() + " in Indexing Server");
				isInter.registryFiles("new",currentFile.getName(), peerID, portNo, dirName);
			}
			System.out.println("Do you want to Search a File, Delete File or Exit? (Search/Delete/Exit)");
			String sd= sc.nextLine();
			while(!sd.equalsIgnoreCase("Exit")){
				if(sd.equalsIgnoreCase("Delete")){
					//Deleting a File from local peer's Directory
					String wantToDel="";
					while(!wantToDel.equalsIgnoreCase("No")){
						System.out.println("Enter the file name which you want to delete");
						String fname = sc.nextLine();
						if(fname!=null){
							File fileToDel = new File(dirName+"\\"+fname);
							// Delete the specified file from local peer's directory
							if(fileToDel.delete()){
								System.out.println("File deleted Successfully.");
								// Updating the index server about the deleted file
								isInter.registryFiles("del",fname, peerID, portNo, dirName);
							}
							else{
								System.out.println("Failed to delete the File");
							}
						}
						else{
							System.out.println("Please Enter a Filename");
						}
						System.out.println("Do you want to delete more files? (Yes/No)");
						wantToDel=sc.nextLine();
					}
				}
				else if(sd.equalsIgnoreCase("Search")){
					//Searching and downloading a File Code
					String ans= "";
					while(!ans.equalsIgnoreCase("No")){
						// Searching the file in Indexing server
						System.out.println("Enter the file name which you want to search");
						fileName = sc.nextLine();
						if(fileName!=null){
							colArr = isInter.searchFile(fileName);			
						}
						else{
							System.out.println("Please Enter a Filename");
						}

						if(!colArr.isEmpty()){					
							// Displaying Peers List which can provide the requested file
							for(ArrayList<String> als : colArr){
								System.out.println("Peer providing the file with Peer ID is "+ als.get(1));
							}
							
							// Choosing one of the returned Peer
							System.out.println("Enter Peer ID you wish to take the file from");
							remotePeer = sc.nextLine();
							
							// Downloading the file from Specified Peer
							int co=colArr.size();
							if(remotePeer!=null){
								for(ArrayList<String> als : colArr){
									if(als.get(1).equalsIgnoreCase(remotePeer)){
										// Looking up from the Registry for Selected Peer
										Registry regis2 = LocateRegistry.getRegistry("localhost", Integer.parseInt(als.get(2)));
										PeerDownloadInterface pdInter = (PeerDownloadInterface) regis2.lookup("root://PeerTest/"+als.get(2)+"/FS");	
										// Calling Remote File Download method of Selected Peer
										byte[] output= pdInter.fileDownload(als);
										System.out.println(output.length);
										// Converting Downloaded byte array into file
										if(output.length!=0){
											FileOutputStream ostream = null;
											try {
												ostream = new FileOutputStream(dirName+"\\"+fileName);
											    ostream.write(output);
											    System.out.println("File Downloading Successful.");
											    System.out.println("Display File " + fileName);
											    //Updating the IndexServer Indexes after downloading the file.
											    isInter.registryFiles("new",fileName, peerID, portNo, dirName);
											}
											catch(Exception e){
												System.out.println("Exception in bytearray to file conversion. " + e.getMessage());
											}
											finally {
											    ostream.close();
											}											
										}
										else{
											System.out.println("File is not present at Remote Location.");
										}
										break;
									}
									else{
										if(co==1)
											System.out.println("Peer with that ID " + remotePeer + " does not exist. Please choose proper PeerId.");
									}
								co--;
								}
							}
							else{
								System.out.println("Please enter proper Peer ID");
							}
						}
						else{
							System.out.println("Sorry, File which you are searching doesnt exist in our Server.");
						}
						System.out.println("Do you want to search again ? (Yes/No)");
						ans=sc.nextLine();
					}				
				}
				else{
					System.out.println("Please select appropriate choice");
				}				
				System.out.println("Do you want to Search a File, Delete File or Exit? (Search/Delete/Exit)");
				sd= sc.nextLine();
			}
			System.exit(0);
		}catch(Exception e) {
			System.out.println("Exception at Client Interface: " + e.getMessage());
		}
	}
	
	// remote File download method is defined 
	public byte[] fileDownload(ArrayList<String> searchedDir) throws RemoteException{
		//0 filename, 1 peerid, 2 port_num, 3 direct
		String fname = searchedDir.get(0);
		String remoteDir=searchedDir.get(3);
		try {
	         File file = new File(remoteDir+"\\"+fname);
//	         System.out.println(file.exists());
	         if(file.exists()){
	     		byte buffer[] = Files.readAllBytes(file.toPath());
		         return buffer;	        	 
	         }
	      }
		catch(Exception e){
	         System.out.println("Error in File download part " + e.getMessage());
	         e.printStackTrace();
	         return new byte[0];
	      }
		return new byte[0];
	}
}