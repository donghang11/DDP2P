package com.HumanDecisionSupportSystemsLaboratory.DDP2P;

import com.HumanDecisionSupportSystemsLaboratory.DDP2P.AndroidChat.ChatMessage;

public class ChatEntity {
	private int avatar;
	private String content;
	private ChatMessage msg;
	private String time;
	//private boolean sent = false;
	private boolean arrived = false;
	private boolean jammed = false;
	private boolean isLeft;
	
	public String toString() {
		return "ChatEntity[left="+isLeft
				+"\n msg="+msg
				+"\n time="+time
				+ "\n arr="+arrived
				+"\n]";
	}
	
	public ChatEntity(int avatar, String text_message, String generalizedTime, boolean isSender_rightAligned) {
		this.avatar = avatar;
		this.content = text_message;
		this.time = generalizedTime;
		this.isLeft = isSender_rightAligned;
	}
	/**
	 * 
	 * @param avatar
	 * @param content
	 * @param time
	 * @param isSender
	 * @param arrived
	 * @param msg
	 */
	public ChatEntity(int avatar, String content, String time, boolean isSender, boolean arrived, ChatMessage msg){
		this.avatar = avatar;
		this.content = content;
		this.time = time;
		this.isLeft = isSender;
		this.setArrived(arrived);
		this.msg = msg;
	}
	public ChatEntity(int avatar, String content, String time, boolean isSender, boolean arrived, boolean jammed, ChatMessage msg){
		this.avatar = avatar;
		this.content = content;
		this.time = time;
		this.isLeft = isSender;
		this.setArrived(arrived);
		this.setJammed(jammed);
		this.msg = msg;
	}
	
	public int getAvatar() {
		return avatar;
	}
	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public boolean isLeft() {
		return isLeft;
	}
	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}
	private boolean isArrived() {
		return arrived;
	}
	private void setArrived(boolean arrived) {
		this.arrived = arrived;
	}
	private boolean isJammed() {
		return jammed;
	}
	private void setJammed(boolean jammed) {
		this.jammed = jammed;
	}
}
