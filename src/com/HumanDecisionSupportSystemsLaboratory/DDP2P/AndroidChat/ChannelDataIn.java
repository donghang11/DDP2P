//package dd_p2p.plugin;
package com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat;
//import dd_p2p.plugin.*;

import java.util.*;
import java.math.BigInteger;

import ASN1.Encoder;
import util.Util;
/**
 * 
 * @author msilaghi
 * Class for handling received data, compacting sequences for acknowledgments.
 */
 public  class ChannelDataIn {
    	private static final boolean DEBUG = false;
		private static Hashtable<String, ChannelDataIn> channels = new Hashtable<String, ChannelDataIn>();
    	String peerGID;
    	byte[] session_ID;
    	BigInteger lastInSequence;
    	BigInteger firstInSequence;
    	ArrayList<BigInteger> outOfSequence = new ArrayList<BigInteger>();
    	String peerName;
    	Calendar time;
    	ArrayList<DatedChatMessage> messages = new ArrayList<DatedChatMessage>();
   	 
	   	 public String toString() {
	   		 return 
	   				 "ChannelDataOut["
	   				 + "\n peerGID = "+peerGID
	   				 + "\n session_ID = "+Util.byteToHex(session_ID)
	   				 + "\n firstInSequence = "+firstInSequence
	   				 + "\n lastInSequence = "+lastInSequence
	   				 + "\n recv = "+Util.concat(outOfSequence, " , ", null)
	   				 + "\n peerName = "+peerName
	   				 //+ "\n sequence_ack = "+sequence_ack
	   				 + "\n time = "+Encoder.getGeneralizedTime(time)
	   				 + "\n msg = "+Util.concat(messages, "\n msg=", null)
	   				 + "\n]";
	   	 }

    	
    	BigInteger getLastInSequence() {
    		return lastInSequence;
    	}
    	ArrayList<BigInteger> getOutOfSequence() {
    		return outOfSequence;
    	}
       	boolean registerIncoming(ChatMessage cmsg) {
     		if ( cmsg.sequence == null ){
        		if (DEBUG) System.out.println("PLUGIN CHAT: ChannelDataIn: register Incomming: why null seq ="+cmsg);
    			return false;
    		}
      	   	return this.registerIncoming(cmsg.session_id, cmsg.first_in_this_sequence , cmsg.sequence);
       	}
    	private boolean registerIncoming(byte[] _session_ID, BigInteger first, BigInteger sequence_crt_in) {
    		if (this.session_ID == null) {
    			this.session_ID = _session_ID;
				this.firstInSequence = first;
			} else {
				if (! Util.equalBytes(session_ID, _session_ID)) {
					this.session_ID = _session_ID;
					this.firstInSequence = first;
					
					this.outOfSequence = new ArrayList<BigInteger>();
					
					//if (sequence_crt_in != null)
					if (first.equals(sequence_crt_in))
						this.lastInSequence = sequence_crt_in;
					else
						this.outOfSequence.add(sequence_crt_in);
					
					return true;
				}
			}
			// when to increment last?
    		if (DEBUG) System.out.println("PLUGIN CHAT: l="+lastInSequence+" f="+firstInSequence+" in="+sequence_crt_in);
    		if ( 
    				//( sequence_crt_in != null ) &&
    				(
    						(
    								(this.lastInSequence == null) 
    								&&
    								(this.firstInSequence.equals(sequence_crt_in))
    						)
    						|| 
    						(
    								(lastInSequence != null)
    								&&
    								sequence_crt_in.equals(lastInSequence.add(BigInteger.ONE))
    						)
    				)
			   )
    		{ // if incoming is the first after the contiguous sequence
    			lastInSequence = sequence_crt_in;
    			
    			for(;;) {
    				if (outOfSequence == null || outOfSequence.size() == 0) break;
    				if (outOfSequence.get(0).equals(lastInSequence.add(BigInteger.ONE))) {
    					lastInSequence = outOfSequence.get(0);
    					outOfSequence.remove(0);
    				} else {
    					break;
    				}
    			}
    			return true;
    		} else {
    			
    			if (firstInSequence != null  && sequence_crt_in.compareTo(this.firstInSequence) <= 0)
    				return false; // duplicate
    			
    			// if another out of sequence
			    if (outOfSequence == null)
			    	outOfSequence = new ArrayList<BigInteger>();
			    return insertInOrderedBinary(outOfSequence, sequence_crt_in);
    		}
    	}
    	
    	/**
    	 * Used to insert a new received message in the list of outOfSequence.
    	 * TODO use binary search.
    	 * @param list
    	 * @param i
    	 */
    	static boolean _insertInOrdered(ArrayList<BigInteger> list, BigInteger i) {
    		for (int k = 0; k < list.size(); k ++) {
    			if (i.compareTo(list.get(k)) < 0) {
    				list.add(k, i); return true;
    			} else {
        			if (i.compareTo(list.get(k)) == 0) { // if already known out of sequence
        				return false;
        			}
    				k++;
    			}
    		}
    		list.add(i);
    		return true;
    	}
    	static boolean insertInOrderedBinary(ArrayList<BigInteger> list, BigInteger i) {
    		if (list.size() == 0) {
    			list.add(i);
    			return true;
    		}
    		return insertInOrderedBinary(list, i, 0, list.size()-1);
    	}
       	static boolean insertInOrderedBinary(ArrayList<BigInteger> list, BigInteger i, int b, int e) {
       		if (b > e) {
       			list.add(b, i);
       			return true;
       		}
       		if (b == e) {
           		int cmp = i.compareTo(list.get(b));
      			if (cmp == 0) {
       				return false;
       			} else {
       				if (cmp < 0) {
       					list.add(b, i);
       					return true;
       				} else {
       					list.add(e + 1, i);
       					return true;
       				}
       			}
       		}
       		int m = (b + e)/2;
       		int cmp = i.compareTo(list.get(m));
       		if (cmp == 0) {
   				return false;
       		}
       		if (cmp < 0) {
       			return insertInOrderedBinary(list, i, b, m);
       		} else {
       			return insertInOrderedBinary(list, i, m+1, e);
       		}
    	}
    	
    	/**
    	 * Create a new channel if absent.
    	 * Only sets the peerGID.
    	 * s
    	 * @param peerGID
    	 * @return
    	 */
    	static ChannelDataIn get(String peerGID) {
    		ChannelDataIn ch = channels.get(peerGID);
    		if (ch == null) {
    			ch = new ChannelDataIn();
    			ch.peerGID = peerGID;
    			//ch.session_ID = ChatMessage.createSessionID();
    			//ch.next_sequence = ch.firstInSequence = BigInteger.ONE;
    			channels.put(peerGID, ch);
    		}
    		return ch;
    	}
    	BigInteger getFirstInSequence() {
    		return firstInSequence;
    	}
    	byte[] getSessionID() {
    		return session_ID;
    	}
    	public static void main(String[] args) {
    		ArrayList<BigInteger> list = new ArrayList<BigInteger>();
    		list.add(new BigInteger("10"));
    		list.add(new BigInteger("12"));
    		list.add(new BigInteger("13"));
    		list.add(new BigInteger("20"));
    		list.add(new BigInteger("25"));
    		boolean r = ChannelDataIn.insertInOrderedBinary(list, new BigInteger(args[0]));
    		for (BigInteger a : list) {
    			System.out.println("=>"+a);
    		}
   			System.out.println("r=>"+r);
    	}
    }


 