package merkleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static merkleClient.HashUtil.md5Java;

public class MerkleValidityRequest {

	/**
	 * IP address of the authority
	 * */
	private final String authIPAddr;
	/**
	 * Port number of the authority
	 * */
	private final int  authPort;
	/**
	 * Hash value of the merkle tree root. 
	 * Known before-hand.
	 * */
	private final String mRoot;
	/**
	 * List of transactions this client wants to verify 
	 * the existence of.
	 * */
	private List<String> mRequests;
	
	/**
	 * Sole constructor of this class - marked private.
	 * */
	private MerkleValidityRequest(Builder b){
		this.authIPAddr = b.authIPAddr;
		this.authPort = b.authPort;
		this.mRoot = b.mRoot;
		this.mRequests = b.mRequest;
	}
	
	/**
	 * <p>Method implementing the communication protocol between the client and the authority.</p>
	 * <p>The steps involved are as follows:</p>
	 * 		<p>0. Opens a connection with the authority</p>
	 * 	<p>For each transaction the client does the following:</p>
	 * 		<p>1.: asks for a validityProof for the current transaction</p>
	 * 		<p>2.: listens for a list of hashes which constitute the merkle nodes contents</p>
	 * 	<p>Uses the utility method {@link #isTransactionValid(String, String, List<String>) isTransactionValid} </p>
	 * 	<p>method to check whether the current transaction is valid or not.</p>
	 * */
	public Map<Boolean, List<String>> checkWhichTransactionValid() throws IOException {
            
		Map<Boolean, List<String>> transactions = new HashMap<>();
                List<String> transactionValid = new ArrayList<>(); //list of verified transactions
                List<String> transactionFailed = new ArrayList<>(); //list of failed transactions
                
                mRequests.stream().forEach(x -> {
                    try {
                        if(isTransactionValid(x,getNodes(x)))  //verify transaction and add to list
                            transactionValid.add(x);
                        else
                            transactionFailed.add(x);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                           
            transactions.put(true, transactionValid);
            transactions.put(false, transactionFailed);
                            
            return transactions;
	}
        
        //getNodes() return the list of merkleNodes
        public List<String> getNodes(String transactionToValidate) throws IOException{ 
            List<String> merkleNodes = new ArrayList<>();
            Socket s = new Socket(authIPAddr, authPort);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true); 
            out.println(transactionToValidate);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String nodeReader = input.readLine(); //nodes reader
                    
            while(nodeReader != null){
                merkleNodes.add(nodeReader);
                nodeReader = input.readLine();
                }
            
            s.close();
            
            return merkleNodes;
        }
	
	/**
	 * 	Checks whether a transaction 'merkleTx' is part of the merkle tree.
	 * 
	 *  @param merkleTx String: the transaction we want to validate
	 *  @param merkleNodes String: the hash codes of the merkle nodes required to compute 
	 *  the merkle root
	 *  
	 *  @return: boolean value indicating whether this transaction was validated or not.
	 * */
	private boolean isTransactionValid(String merkleTx, List<String> merkleNodes) {
            String checkRoot = md5Java(merkleTx); //computed root
            merkleNodes.stream().forEach(x -> checkRoot.concat(md5Java(x))); 
            
            return mRoot.equals(checkRoot);
	}

	/**
	 * Builder for the MerkleValidityRequest class. 
	 * */
	public static class Builder {
		private String authIPAddr;
		private int authPort;
		private String mRoot;
		private List<String> mRequest;	
		
		public Builder(String authorityIPAddr, int authorityPort, String merkleRoot) {
			this.authIPAddr = authorityIPAddr;
			this.authPort = authorityPort;
			this.mRoot = merkleRoot;
			mRequest = new ArrayList<>();
		}
				
		public Builder addMerkleValidityCheck(String merkleHash) {
			mRequest.add(merkleHash);
			return this;
		}
		
		public MerkleValidityRequest build() {
			return new MerkleValidityRequest(this);
		}
	}
}