package com.filetransfer;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.Map.Entry; 

public class IndexServerImpl implements IndexServerInterface {
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    //get current date time with Date()
    Date date = new Date();
    
	// Defining a hash map for indexing the file details
    // We are using MultiValue Hash Map Data Structure for storing Multiple Entries for a single filename. 
    // For each Filename(Key), there will be collection of entries(Value).
	private MultivaluedMap<String, ArrayList<String>> fileDictionary = new MultivaluedHashMap<>();
	
	// For Adding or Removal of Entry in HashMap Index
	@Override
	public synchronized void registryFiles(String rd,String filename, String peerid, String port_num, String directory) throws RemoteException {
		// Indexing the File details -- "new"
		if(rd.equalsIgnoreCase("new")){
			// checking for duplicate record in Index
			if(this.fileDictionary.containsKey(filename)){
				Collection<ArrayList<String>> delArrFile = this.fileDictionary.get(filename);
				for(ArrayList<String> als : delArrFile){
					if(als.get(1).equalsIgnoreCase(peerid)){
						// Duplicate record for Indexing. so Rejecting it
						return;
					}
				}
			}
			// If no duplicate, then indexing it
			ArrayList<String> arrFileDtl = new ArrayList<String>();
			arrFileDtl.add(filename);
			arrFileDtl.add(peerid);
			arrFileDtl.add(port_num);
			arrFileDtl.add(directory);
			//System.out.println(arrFileDtl);
			this.fileDictionary.add(filename, arrFileDtl);
		}
		
		// Updating the Index after deletion of a file -- "del"
		else if(rd.equalsIgnoreCase("del")){
			Collection<ArrayList<String>> delArrFile = new ArrayList<ArrayList<String>>();
			Collection<ArrayList<String>> onceMore = new ArrayList<ArrayList<String>>();
			
			// Checking if deleted File Name is present in Index
			if(this.fileDictionary.containsKey(filename)){
				delArrFile = this.fileDictionary.get(filename);
				for(ArrayList<String> als : delArrFile){
					// Removing the Peer Entry from Index
					if(als.get(1).equalsIgnoreCase(peerid)){
						//System.out.println(this.fileDictionary.get(filename));
						onceMore= this.fileDictionary.remove(filename);
						//System.out.println("BEFORE"+onceMore);
						onceMore.remove(als);
						//System.out.println("AFTER" + onceMore);
						for (ArrayList<String> p : onceMore){
							this.fileDictionary.add(filename,p);							
						}
						System.out.println("Index Server Updated & Specified Record Deleted");
					}
				}
			}
			else{
				System.out.println("Delete Request: No entry detected for filename");
			}
		}
		else{
			System.out.println("Invalid Request.");
		}
		// Displaying the Index after every Addition or Removal of Entry.
		System.out.println("####################################");
		System.out.println("THE UPDATED INDEX at " + dateFormat.format(date));
		for (Entry<String, List<ArrayList<String>>> entry : this.fileDictionary.entrySet()) {
		    System.out.println(entry.getKey() + " => " + entry.getValue());
		}
		System.out.println("####################################");
	}

	// Searching specified Filename entry from Index and returns a Collection of ArrayList
	@Override
	public synchronized Collection<ArrayList<String>> searchFile(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		Collection<ArrayList<String>> resultArrFile = new ArrayList<ArrayList<String>>();
		if(this.fileDictionary.containsKey(filename)){
			resultArrFile = this.fileDictionary.get(filename);
		}
		return resultArrFile;
	}
}
