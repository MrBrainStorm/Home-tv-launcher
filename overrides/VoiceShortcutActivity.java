package com.gathin.tvhome;
import android.app.Activity;
import android.os.Bundle;
public final class VoiceShortcutActivity extends Activity {
 @Override public void onCreate(Bundle state){super.onCreate(state);RemoteKeys.launch(this,"com.google.android.youtube.tv");finish();}
}
