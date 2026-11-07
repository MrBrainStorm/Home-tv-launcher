package com.gathin.tvhome;
import android.content.Context;
import java.io.*;
final class HomeAssets {
 static synchronized void install(Context c) {
  File dir=new File(c.getFilesDir(),"home-theme");
  if(new File(dir,".v2").exists())return;
  try {copy(c,"home-theme",dir);new File(dir,".v2").createNewFile();}
  catch(IOException e){android.util.Log.e("HomeAssets","Theme install failed",e);}
 }
 static void copy(Context c,String path,File out)throws IOException {
  String[] list=c.getAssets().list(path);
  if(list!=null&&list.length>0){if(!out.isDirectory()&&!out.mkdirs())throw new IOException("mkdir");for(String name:list)copy(c,path+"/"+name,new File(out,name));}
  else try(InputStream in=c.getAssets().open(path);OutputStream dest=new FileOutputStream(out)){byte[] b=new byte[16384];int n;while((n=in.read(b))!=-1)dest.write(b,0,n);}
 }
}
