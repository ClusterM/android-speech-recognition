package com.example.speechtests;

import com.Cluster.SpeechRecognizer.SpeechRecognizer;
import com.Cluster.SpeechRecognizer.SpeechRecognizer.VoiceRecognizedListener;
import com.example.voicetests.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MainActivity extends Activity implements VoiceRecognizedListener
{
	static SpeechRecognizer recognizer;
	boolean ready = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (recognizer == null)
			recognizer = new SpeechRecognizer();
		recognizer.setLanguage("ru-RU");
		recognizer.setVoiceRecognizedListener(this);
		try
		{
			recognizer.start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		handlerShowDetectLevel.sendEmptyMessage(0);
	}

	@Override
	protected void onDestroy()
	{
		recognizer.stop();
		recognizer = null;
		super.onDestroy();
	}

	@Override
	public void onVoiceRecognized(String[] results)
	{
		Message msg = new Message();
		msg.obj = results;
		handlerShowResults.sendMessage(msg);
	}

	Handler handlerShowResults = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			StringBuilder out = new StringBuilder();
			String[] results = (String[]) msg.obj;
			for (String result : results)
			{
				out.append(result);
				out.append("\r\n");
			}
			((TextView) findViewById(R.id.text)).setText(out.toString());
		}
	};

	Handler handlerShowDetectLevel = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (recognizer != null)
			{
				int detectLevel = recognizer.getDetectLevel();
				((TextView) findViewById(R.id.detect_level)).setText("Current detect volume level: " + detectLevel);
				if (!ready && detectLevel < 32767)
				{
					ready = true;
					((TextView) findViewById(R.id.text)).setText(R.string.speak);
				}
			}
			handlerShowDetectLevel.sendEmptyMessageDelayed(0, 500);
		}
	};
}
