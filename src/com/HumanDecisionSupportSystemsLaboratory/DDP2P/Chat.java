package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.AndroidChatReceiver;
import com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.Main;

import data.D_Peer;
import data.HandlingMyself_Peer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Chat extends Activity {
	EditText chat_input;
	private String chatContent;//消息内容
	ListView chatListView;
	public List<ChatEntity> chatEntityList=new ArrayList<ChatEntity>();//所有聊天内容
	private int safe_position;
	private String safe_lid;
	private String safe_gidh;
	private int chatAccount;
	private String chatName;
	public static int[] avatar=new int[]{};
	private static Chat crtChat = null;
	MyBroadcastReceiver br;
	private D_Peer peer;
	public D_Peer getPeer() {
		return peer;
	}
	public static Chat getCrtChat() {
		return crtChat;
	}
	public void setChatEntityList (List<ChatEntity> chatEntityList) {
		this.chatEntityList = chatEntityList;
		chatListView=(ListView) findViewById(R.id.chat_listview);
		chatListView.setAdapter(new ChatAdapter(this,chatEntityList));
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_window);
		
		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		
		//top panel setting
		safe_position = b.getInt("Safe_ID");
		safe_lid = b.getString("Safe_LID");
		safe_gidh = b.getString("Safe_GIDH");
	//	chatAccount=getIntent().getIntExtra("account", 0);
		chatName=b.getString("who");
/*		ImageView avatar_iv=(ImageView) findViewById(R.id.chat_top_avatar);
		avatar_iv.setImageResource(avatar[chatAvatar]);*/
		TextView top_name=(TextView) findViewById(R.id.chat_top_name);
		top_name.setText(chatName);
		
		peer = D_Peer.getPeerByLID(safe_lid, true, false);
		crtChat = this;
		//this.setChatEntityList(AndroidChatReceiver.getListEntity(peer.getGID()));
		try {
			this.chatEntityList = AndroidChatReceiver.getListEntity(peer.getGID());
		} catch(Exception e) {}
		if (this.chatEntityList == null) {
			this.chatEntityList = new ArrayList<ChatEntity>();
		}
		chat_input=(EditText) findViewById(R.id.chat_input);
		findViewById(R.id.chat_send).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
					chatContent=chat_input.getText().toString();
					chat_input.setText("");
					updateChatView(new ChatEntity(
                            0,
							chatContent,
				    		MyTime.geTime(),
				    		false));
					com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.Main.sendMessage(chatContent, peer.getGID(), peer);
					//AndroidChatReceiver.addSentMessage();
/*				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		});
		 //注册广播
		IntentFilter myIntentFilter = new IntentFilter(); 
        myIntentFilter.addAction("org.yhn.yq.mes");
        br=new MyBroadcastReceiver();
        registerReceiver(br, myIntentFilter);

	}
	@Override
	public void finish() {
		 unregisterReceiver(br);
		super.finish();
	}
	
	//broadcast receiver
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String[] mes = intent.getStringArrayExtra("message");
		    //更新聊天内容
		    updateChatView(new ChatEntity(
		    		Integer.parseInt(mes[2]),
		    		mes[3],
		    		mes[4],
		    		true));
		}
	}
	public void updateChatView(ChatEntity chatEntity){
		chatEntityList.add(chatEntity);
		chatListView=(ListView) findViewById(R.id.chat_listview);
		chatListView.setAdapter(new ChatAdapter(this,chatEntityList));
	}
	public String getPeerGID() {
		// TODO Auto-generated method stub
		return getPeer().getGID();
	}
	public void setPeerName(String peerName) {
		// TODO Auto-generated method stub
		
	}

}
