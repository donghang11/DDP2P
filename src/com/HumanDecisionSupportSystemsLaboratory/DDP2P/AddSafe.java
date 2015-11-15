package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import config.Application_GUI;
import ciphersuits.Cipher;
import ciphersuits.CipherSuit;
import hds.PeerInput;
import data.D_Peer;
import data.HandlingMyself_Peer;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;


public class AddSafe extends ActionBarActivity {
	
	private String name, email, slogan;
    	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_safe);	
			
		Application_GUI.dbmail = new Android_DB_Email(this);
		Application_GUI.gui = new Android_GUI();
		
    	final Button but = (Button) findViewById(R.id.submit);
    	final EditText add_name = (EditText) findViewById(R.id.add_name);
    	final EditText add_email = (EditText) findViewById(R.id.add_email);
    	final EditText add_slogan = (EditText) findViewById(R.id.add_slogan);
    	
    	but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	name = add_name.getText().toString();
            	email = add_email.getText().toString();
            	slogan = add_slogan.getText().toString();            	
            	
                newPeer(name, email, slogan);
                
                
            }
        });
        
	}
  
	
	//return button on left-top corner
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
		
        return super.onOptionsItemSelected(item);
	}

	//function for add a new peer
	private D_Peer newPeer(String name, String email, String slogan) {

		PeerInput pi = new PeerInput();
		pi.name = name;
		pi.email = email;
		pi.slogan = slogan;
		
		/*
		 * ciphersuite did not initialize, add a new ciphersuit class
		 */
		
		CipherSuit cs = new CipherSuit();
		cs.cipher = Cipher.RSA;
		cs.hash_alg = Cipher.SHA256;
		cs.ciphersize = 2048;

		pi.cipherSuite = cs;
		
		D_Peer peer = HandlingMyself_Peer.createMyselfPeer_w_Addresses(pi, true);
		peer.setEmail(email);
		peer.setSlogan(slogan);
		peer.storeRequest();
	
		data.HandlingMyself_Peer.setMyself(peer, true);
		
        Toast.makeText(this, "add a new safe successfully!", Toast.LENGTH_LONG).show();
		return peer;
	}


}
