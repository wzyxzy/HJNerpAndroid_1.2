package com.hjnerp.util;

import java.util.List;

public interface Command
{
	void action();
	
	interface OnResultListener
	{
		void onResult(boolean success);
	}
	
	interface OnMultiResultListener
	{
		void onResult(List<Boolean> successes);
	}
}
