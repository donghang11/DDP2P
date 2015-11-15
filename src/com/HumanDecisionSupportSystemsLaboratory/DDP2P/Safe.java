package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.DBInterface;
import util.P2PDDSQLException;
import util.Util;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import config.Application;
import config.Application_GUI;
import config.DD;
import config.Identity;
import data.D_Peer;
import data.HandlingMyself_Peer;

public class Safe extends android.support.v4.app.ListFragment implements OnItemClickListener{
	
	private String data[][];
	
	private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	private SimpleAdapter simpleAdapter=null;
	
	private static ArrayList<D_Peer> peers = new ArrayList<D_Peer>();
	
	public static ArrayList<D_Peer> getPeers() {
		return peers;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		

		/*
		 * very IMPORTANT! The list and peers must be clear every time when the Fragment
		 * get created, otherwise, duplicate entries might occur!!!
		 */
		list.clear();
		peers.clear();
		
		//pull out all safes from database		
		Application_GUI.dbmail = new Android_DB_Email(this.getActivity());
		Application_GUI.gui = new Android_GUI();
		try {			

			DBInterface db = new DBInterface("deliberation-app.db");
			Application.db = db;
			ArrayList<ArrayList<Object>> peer_IDs = D_Peer.getAllPeers();								
        	for (ArrayList<Object> peer_data : peer_IDs) {
		        if (peer_data.size() <= 0) continue;
		        String p_lid = Util.getString(peer_data.get(0));
		        D_Peer peer = D_Peer.getPeerByLID(p_lid, true, false);		        
		        if (peer == null) continue;
		        peers.add(peer);
	        }	
        	data = new String[peers.size()][];	
        	for ( int k = 0 ; k < peers.size(); k ++ ) {
		        D_Peer p = peers.get(k);
		        
		        //if a safe has private key then use getname... to be implemented
		        data[k] = new String[] {p.getName_MyOrDefault(), p.getEmail(), p.getSlogan_MyOrDefault()};  
	        }
        	       				
		} catch (P2PDDSQLException e1) {
			e1.printStackTrace();
		}
		
/*		Identity.init_Identity();
		
		HandlingMyself_Peer.loadIdentity(null);
		
		try {
			DD.startUServer(true, Identity.current_peer_ID);
			DD.startServer(false, Identity.current_peer_ID);
			DD.startClient(true);
			
		} catch (NumberFormatException | P2PDDSQLException e) {
			System.err.println("Safe: onCreateView: error");
			e.printStackTrace();
		}
		
		try {
			DD.load_listing_directories();
		} catch (NumberFormatException | UnknownHostException
				| P2PDDSQLException e) {
			e.printStackTrace();
		}
		D_Peer myself = HandlingMyself_Peer.get_myself();
		myself.addAddress(Identity.listing_directories_addr.get(0), true, null);*/
	

		//end of data pulling out
		

		//using a map datastructure to store data for listview
		for(int i=0; i<this.data.length;i++) {
			Map<String, String> map = new HashMap<String,String>();
			map.put("pic", String.valueOf(R.drawable.placeholder));
			map.put("name", this.data[i][0]);
			map.put("email", this.data[i][1]);
			map.put("slogan", this.data[i][2]);
			this.list.add(map);
		}
		
		//set up simple adapter for listview
        this.simpleAdapter = new SimpleAdapter(getActivity(), this.list, R.layout.safe_list,
    		new String[]{"pic","name","email","slogan","score"}, 
    		new int[] {R.id.pic,R.id.name,R.id.email,R.id.slogan});
		
        setListAdapter(simpleAdapter);
        
		return super.onCreateView(inflater, container, savedInstanceState);	
	}	

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
        getListView().setOnItemClickListener(this);

		super.onActivityCreated(savedInstanceState);
	}

	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {		
		Intent myIntent = new Intent();
		myIntent.setClass(getActivity(), SafeProfile.class);
		D_Peer peer = null;

		ArrayList<D_Peer> p = Safe.getPeers();
		try {
			if (position >= p.size()) return;
			peer = p.get(position);
		} catch (Exception e) {return;}
		//pass data to profile
		myIntent.putExtra("who", data[position][0]);
		myIntent.putExtra("Safe_ID", position);
		myIntent.putExtra("Safe_GIDH", peer.getGIDH());
		myIntent.putExtra("Safe_LID", peer.getLIDstr());
		myIntent.putExtra("profImg", String.valueOf(R.drawable.placeholder));
		startActivity(myIntent);     		
	}

}
