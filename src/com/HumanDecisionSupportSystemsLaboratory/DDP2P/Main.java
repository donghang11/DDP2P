package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import java.io.File;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.AndroidChatReceiver;

import util.DBInterface;
import util.P2PDDSQLException;
import util.StegoStructure;
import config.Application;
import config.Application_GUI;
import config.DD;
import config.Identity;
import data.D_Peer;
import data.HandlingMyself_Peer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;



public class Main extends FragmentActivity implements TabListener{
	
	android.app.ActionBar actionBar = null;
	
	ViewPager mViewPager;

	TabFragmentPagerAdapter mAdapter;
	
	int SELECT_PHOTO = 42;
	int SELECT_PHOTO_KITKAT = 43;
	
	private String selectedImagePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
        
		//initial action bar
		actionBar = this.getActionBar();
        
		//set tab navigation mode
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//initial view pager
        mViewPager = (ViewPager) this.findViewById(R.id.pager);
                        
        actionBar.setDisplayHomeAsUpEnabled(false);  
        
        //add adapter
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());  
            
        mViewPager.setAdapter(mAdapter);  
            
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		
        });
        
		//add tabs
		actionBar.addTab(actionBar.newTab().setText("Safes")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Orgs")
				.setTabListener(this));		
		actionBar.addTab(actionBar.newTab().setText("Acts")
				.setTabListener(this));
	
 
    }//end of onCreate()

		

	@Override
	public void onTabReselected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		
	}

	//initial menu in action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		//initial the server:
		Identity.init_Identity();
		
		HandlingMyself_Peer.loadIdentity(null);
		
		try {
			DD.load_listing_directories();
		} catch (NumberFormatException | UnknownHostException
				| P2PDDSQLException e) {
			Log.i("server", "some error in server initial!");
			e.printStackTrace();
		}
		D_Peer myself = HandlingMyself_Peer.get_myself();
		myself.cleanAddresses(true, null);
		myself.cleanAddresses(false, null);
		hds.Address dir0 = Identity.listing_directories_addr.get(0);
		dir0.pure_protocol = hds.Address.DIR;
		dir0.branch = DD.BRANCH;
		dir0.agent_version = DD.VERSION;
		dir0.certified = true;
		dir0.version_structure = hds.Address.V3;
		dir0.address = dir0.domain+":"+dir0.tcp_port;
		System.out.println("Adding address: "+dir0);
		myself.addAddress(dir0, true, null);
		System.out.println("Myself After Adding address: "+myself);
		Log.i("myself", myself.toString());

		
		try {
			DD.startUServer(true, Identity.current_peer_ID);
			DD.startServer(false, Identity.current_peer_ID);
			DD.startClient(true);
			
		} catch (NumberFormatException | P2PDDSQLException e) {
			System.err.println("Safe: onCreateView: error");
			e.printStackTrace();
		}
		
		Log.i("Test peer", "test peer...");

		//initial chat:
		try {
			plugin_data.PluginRegistration.loadPlugin(com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.Main.class, HandlingMyself_Peer.getMyPeerGID(), HandlingMyself_Peer.getMyPeerName());
			com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.Main.receiver = new AndroidChatReceiver();
		} catch (MalformedURLException e) {
			Log.i("chat", "some error in chat initial!");
			e.printStackTrace();
		}
		
		
		return super.onCreateOptionsMenu(menu);
	}
	
    //the plus mark, add a new safe
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.add_new_safe_my) {
			Toast.makeText(this, "add a new safe my", Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent();
			intent.setClass(this, AddSafe.class);
			startActivity(intent);
		}

		if (item.getItemId() == R.id.add_new_safe_other) {
			Toast.makeText(this, "add a new safe other", Toast.LENGTH_SHORT).show();
		    
		    if (Build.VERSION.SDK_INT <19){
		        Intent intent = new Intent(); 
		        intent.setType("image/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(intent, SELECT_PHOTO);
		    } else {
		        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		        intent.setType("image/*");
		        startActivityForResult(intent, SELECT_PHOTO_KITKAT);
		    }
		}
		
		
		
		return super.onOptionsItemSelected(item);
	}
		
    
    //adapter
    public static class TabFragmentPagerAdapter extends FragmentPagerAdapter{  
    	  
        public TabFragmentPagerAdapter(FragmentManager fm) {  
            super(fm);  
        }  
  
        @Override  
        public Fragment getItem(int pos) {  
            Bundle bun = new Bundle();

            switch (pos) {   
            
            case 0:
            	Safe mainAct = new Safe();
                bun.putInt("pageNo", pos+1);
            	mainAct.setArguments(bun);
            	
                return mainAct;
            	
            case 1:  
                Orgs orgs = new Orgs();       
                bun.putInt("pageNo", pos+1);
                orgs.setArguments(bun);
                return orgs;
                
            case 2:
                Acts chat = new Acts();       
                bun.putInt("pageNo", pos+1);
                chat.setArguments(bun);
                return chat;
            }  
            
            return null;
        }  
  
        @Override  
        public int getCount() {  
              
            return 3;  
        }   
    }


	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {        
		if (resultCode == RESULT_OK && resultData != null) {
            Uri uri = null;
            
            if (requestCode == SELECT_PHOTO) {
                uri = resultData.getData();
                Log.i("Uri", "Uri: " + uri.toString());
            } else if (requestCode == SELECT_PHOTO_KITKAT) {
                uri = resultData.getData();
                Log.i("Uri_kitkat", "Uri: " + uri.toString());
                final int takeFlags = resultData.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
            }
            
 
            selectedImagePath = FileUtils.getPath(this,uri);
            Log.i("path", "path: " + selectedImagePath); 
                
            File selectImageFile = new File(selectedImagePath);
            String error;    
            StegoStructure adr[] = DD.getAvailableStegoStructureInstances();
            int[] selected = new int[1];
            try {
            	error = DD.loadBMP(selectImageFile, adr, selected);
            	Log.i("error", "error: " + error); 
			    if (error == "") {
			        adr[selected[0]].save();
			        Toast.makeText(this, "add new safe other successfully!", Toast.LENGTH_SHORT).show();
			    }						
            }
		    catch (Exception e) {
		    	Toast.makeText(this, "Unable to load safe from this photo!", Toast.LENGTH_SHORT).show();
			    e.printStackTrace();
		    } 
                	
		}
		super.onActivityResult(requestCode, resultCode, resultData);
	}
		
}
