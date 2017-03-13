package com.hjnerp.websocket;

public interface OnNotificationListener
{
	void onNotification(String msg);
	void onError(String msg);
}