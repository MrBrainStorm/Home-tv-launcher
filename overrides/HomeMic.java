package com.gathin.tvhome;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import com.cgutman.adblib.*;
import java.io.File;
import java.net.*;
import java.nio.charset.StandardCharsets;
final class HomeMic {
 private static boolean started;
 static synchronized void start(Context context){
  if(started)return;started=true;Context app=context.getApplicationContext();
  new Thread(()->run(app),"local-microphone").start();
 }
 private static void run(Context app){
  AdbBase64 base64=data->Base64.encodeToString(data,Base64.NO_WRAP);
  File privateKey=new File(app.getFilesDir(),"local-adb-private"),publicKey=new File(app.getFilesDir(),"local-adb-public");
  AdbCrypto crypto;
  try{if(privateKey.exists()&&publicKey.exists())crypto=AdbCrypto.loadAdbKeyPair(base64,privateKey,publicKey);else{crypto=AdbCrypto.generateAdbKeyPair(base64);crypto.saveAdbKeyPair(privateKey,publicKey);}}
  catch(Exception e){Log.e("HomeMic","Cannot create local key",e);return;}
  Handler handler=new Handler(Looper.getMainLooper());long last=0;
  while(true){
   Socket socket=new Socket();AdbConnection connection=null;
   try{
    socket.connect(new InetSocketAddress("127.0.0.1",5555),5000);socket.setSoTimeout(120000);
    connection=AdbConnection.create(socket,crypto);connection.connect();socket.setSoTimeout(0);
    Log.i("HomeMic","Local ADB connected");AdbStream stream=connection.open("shell:getevent -lt");StringBuilder pending=new StringBuilder();
    while(true){pending.append(new String(stream.read(),StandardCharsets.UTF_8));int newline;
     while((newline=pending.indexOf("\n"))>=0){String line=pending.substring(0,newline);pending.delete(0,newline+1);
      if(line.contains("KEY_VOICECOMMAND")&&line.contains(" UP")&&SystemClock.elapsedRealtime()-last>1000){last=SystemClock.elapsedRealtime();Log.i("HomeMic","Microphone -> YouTube");handler.post(()->RemoteKeys.launch(app,"com.google.android.youtube.tv"));}
     }
     if(pending.length()>16384)pending.setLength(0);
    }
   }catch(Exception e){Log.w("HomeMic","Local connection waiting: "+e.getClass().getSimpleName());}
   finally{try{if(connection!=null)connection.close();socket.close();}catch(Exception ignored){}}
   try{Thread.sleep(15000);}catch(InterruptedException e){Thread.currentThread().interrupt();return;}
  }
 }
}
