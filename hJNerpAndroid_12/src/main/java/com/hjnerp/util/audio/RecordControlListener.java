package com.hjnerp.util.audio;

public interface RecordControlListener {

	void startRecording(String paramString);

	void cancelRecording();

	int stopRecording();

	boolean isRecording();

	String getRecordFilePath(String paramString);
}
