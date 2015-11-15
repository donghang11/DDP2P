package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import data.D_Peer;
import data.D_PeerInstance;


public class SafeProfile extends FragmentActivity {
	
	private ImageView imgbut;
	private TextView whoText;
	private int safe_id;
	private String safe_gidh;
	private String safe_lid;
	private D_Peer peer;
	private String whoStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		//enable action bar home button
		android.app.ActionBar actBar = getActionBar();
		actBar.setDisplayHomeAsUpEnabled(true);
		
		
		//retrieve text and image from main activity
		imgbut = (ImageView)findViewById(R.id.profImg);  //profile img
		whoText = (TextView)findViewById(R.id.profName); //profile name
		
		Intent i = this.getIntent();
		Bundle b = i.getExtras();
				
		whoStr = b.getString("who");		
		safe_gidh = b.getString("Safe_GIDH");		
		safe_lid = b.getString("Safe_LID");		
		
		safe_id = b.getInt("Safe_ID") + 1;
		
		whoText.setText(whoStr);
		
		int imgPath = Integer.parseInt(b.getString("profImg"));
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),imgPath);
		imgbut.setImageBitmap(bmp);
					
		//safe id
        //String sid = String.valueOf(safe_id);
        
		//ArrayList<D_Peer> peers = Safe.getPeers();
        //peer = peers.get(safe_id);

        peer = D_Peer.getPeerByLID(safe_lid, true, false);
        
        
        if (peer.getSK()!=null) {
            Button setName = (Button)findViewById(R.id.button_set_name);   
            setName.setVisibility(View.VISIBLE);
            setName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				   	Bundle id = new Bundle();
					id.putInt("Safe_ID", safe_id); 
							
					//update name dialog
					FragmentManager fm = getSupportFragmentManager();
				    UpdateSafeName nameDialog = new UpdateSafeName();
				    nameDialog.setArguments(id);
				    nameDialog.show(fm, "fragment_edit_name");	
					
				}
			});
            
            Button setMyself = (Button)findViewById(R.id.button_set_myself);   
            setMyself.setVisibility(View.VISIBLE);
            setMyself.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					data.HandlingMyself_Peer.setMyself(peer, true);					
				}
			});            
            

            
        }else {
            Switch hideThisSafe = (Switch)findViewById(R.id.switch_hide_this_safe);   
            hideThisSafe.setVisibility(View.VISIBLE);
            
            if (peer.getHidden()==false)
            	hideThisSafe.setChecked(false);
            else
            	hideThisSafe.setChecked(true);
            
            hideThisSafe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			        if (isChecked) {
			        	D_Peer.setHidden(peer, true);
			    		peer.storeRequest();
			    		peer.releaseReference();
			        } else {
			        	D_Peer.setHidden(peer, false);
			    		peer.storeRequest();
			    		peer.releaseReference();
			        }
					
				}
			});
            
            Button resetLastSyncDate = (Button)findViewById(R.id.button_reset_LastSyncDate);   
            resetLastSyncDate.setVisibility(View.VISIBLE);
            resetLastSyncDate.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				peer = D_Peer.getPeerByPeer_Keep(peer);
    			    peer.setLastSyncDate(null);
    				peer.storeRequest();
    				peer.releaseReference();

    				for (D_PeerInstance i: peer._instances.values()) {
    				   Calendar date =  i.get_last_sync_date();
    				   Log.i("last_sync_date", "last sync date: " + date);
    				}
    			}
    		});
            
            Button sendMsg = (Button)findViewById(R.id.button_send_msg);   
            sendMsg.setVisibility(View.VISIBLE);
            sendMsg.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {

    				Intent myIntent = new Intent();
    				myIntent.setClass(SafeProfile.this, Chat.class);
    				
    				//pass data to chat
    				myIntent.putExtra("who", whoStr);
    				myIntent.putExtra("Safe_ID", safe_id);
    				myIntent.putExtra("Safe_LID", safe_lid);
    				myIntent.putExtra("Safe_GIDH", safe_gidh);
    				myIntent.putExtra("profImg", String.valueOf(R.drawable.placeholder));
    				startActivity(myIntent);
    			}
    		});
        }
        
	    
        //set up switch access
        Switch accessIt = (Switch)findViewById(R.id.switch_access);   
        
        if (peer.getUsed()==true) 
        	accessIt.setChecked(true);
        if (peer.getUsed()==false)
        	accessIt.setChecked(false);
                
        accessIt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	D_Peer.setUsed(peer,true);
		        } else {
		        	D_Peer.setUsed(peer,false);
		        }		        		        
			}
		});
        
       //set up switch block
        Switch blockIt = (Switch)findViewById(R.id.switch_block);   
        
        if (peer.getBlocked()==true) 
        	blockIt.setChecked(true);
        if (peer.getBlocked()==false)
        	blockIt.setChecked(false);
                
        blockIt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	D_Peer.setBlocked(peer,true);
		        } else {
		        	D_Peer.setBlocked(peer,false);
		        }		        		        
			}
		});
        
        //set up switch serve
        Switch serveIt = (Switch)findViewById(R.id.switch_serve);   
        
        if (peer.getUsed()==true) 
        	serveIt.setChecked(true);
        if (peer.getUsed()==false)
        	serveIt.setChecked(false);
                
        serveIt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	D_Peer.setUsed(peer, true);
		    		peer.storeRequest();
		    		peer.releaseReference();
		        } else {
		        	D_Peer.setUsed(peer, false);
		    		peer.storeRequest();
		    		peer.releaseReference();
		        }		        		        
			}
		});
               
    }
  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		

		//return button on left-top corner
		if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
		
		else if (item.getItemId() == R.id.action_setNameMy) {
		   	Bundle id = new Bundle();
			id.putInt("Safe_ID", safe_id); 
					
			//update name my dialog
			FragmentManager fm = getSupportFragmentManager();
		    UpdateSafeNameMy nameDialog = new UpdateSafeNameMy();
		    nameDialog.setArguments(id);
		    nameDialog.show(fm, "fragment_edit_name_my");
		}
		
		else if (item.getItemId() == R.id.action_setEmail) {
		   	Bundle id = new Bundle();
			id.putInt("Safe_ID", safe_id); 
					
			//update email dialog
			FragmentManager fm = getSupportFragmentManager();
		    UpdateSafeEmail emailDialog = new UpdateSafeEmail();
		    emailDialog.setArguments(id);
		    emailDialog.show(fm, "fragment_edit_email");
			
		}
		
		else if (item.getItemId() == R.id.action_setSlogan) {
		   	Bundle id = new Bundle();
			id.putInt("Safe_ID", safe_id); 
					
			//update slogan dialog
			FragmentManager fm = getSupportFragmentManager();
		    UpdateSafeSlogan sloganDialog = new UpdateSafeSlogan();
		    sloganDialog.setArguments(id);
		    sloganDialog.show(fm, "fragment_edit_slogan");
		}
					
		else	
			Toast.makeText(this, "to be implement", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_main, menu);
		return true;
	}
	
/*    public void onAccessItClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        
        if (on) {
        	
        	D_Peer.setUsed(peer, false);
    		peer.storeRequest();
    		peer.releaseReference();
        } else {
        	D_Peer.setUsed(peer, true);
    		peer.storeRequest();
    		peer.releaseReference();
        }
    }*/

}
