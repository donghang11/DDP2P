package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import data.D_Peer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateSafeName extends DialogFragment {
	
	private D_Peer peer;
	private String sName;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {  	
    	
    	View view = inflater.inflate(R.layout.diaglog__profile_update, container);
    	final Button but = (Button) view.findViewById(R.id.update_safe_ok);    	
        final EditText name = (EditText) view.findViewById(R.id.text_update_safe);
                       
        getDialog().setTitle("Name");
        
        Bundle bund = getArguments();
        int id = bund.getInt("Safe_ID");       
        String sid = String.valueOf(id);
        
        peer = D_Peer.getPeerByLID(sid, true, false);
        
    	but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {           
            	sName = name.getText().toString();
            	updateName(peer, sName);
            	Toast.makeText(getActivity(), "update successfully!", Toast.LENGTH_LONG).show();
            }
        });
        
        return view;
    }
    
	void updateName(D_Peer peer, String newName) {
		D_Peer p = D_Peer.getPeerByPeer_Keep(peer);
		p.setName(newName);
		p.storeRequest();
		p.releaseReference();
	}

}
