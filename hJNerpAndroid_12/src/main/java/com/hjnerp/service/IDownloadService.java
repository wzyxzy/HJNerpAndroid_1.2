package com.hjnerp.service;

import android.os.Handler;

public interface IDownloadService
{
	void scheduleSilentGetImage(String uri, String fileName, Handler handler);
}
