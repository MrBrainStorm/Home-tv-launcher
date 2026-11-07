package com.gathin.tvhome;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
final class HomeWeather {
 private final Activity activity; private final TextView main,other; private final Handler handler=new Handler(Looper.getMainLooper());
 private final SharedPreferences cache; private boolean active,busy; private long lastFetch;
 private final Runnable tick=()->{if(active)refresh();};
 HomeWeather(Activity a,FrameLayout root){
  activity=a;cache=a.getSharedPreferences("home_weather",0);
  LinearLayout box=new LinearLayout(a);box.setOrientation(LinearLayout.VERTICAL);box.setFocusable(false);
  FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.LEFT);p.leftMargin=dp(60);p.topMargin=dp(150);
  main=new TextView(a);main.setTextSize(25);main.setTextColor(Color.WHITE);main.setTypeface(Typeface.create("sans-serif-light",0));main.setShadowLayer(5,0,2,Color.BLACK);
  other=new TextView(a);other.setTextSize(14);other.setTextColor(0xDDFFFFFF);other.setPadding(0,dp(9),0,0);other.setShadowLayer(4,0,2,Color.BLACK);
  main.setText(cache.getString("main","Кулешовка · загрузка погоды…"));other.setText(cache.getString("other","Ростов-на-Дону · Азов"));box.addView(main);box.addView(other);root.addView(box,p);
 }
 int dp(int n){return Math.round(n*activity.getResources().getDisplayMetrics().density);}
 void start(){active=true;refresh();}
 void stop(){active=false;handler.removeCallbacks(tick);}
 void refresh(){handler.removeCallbacks(tick);if(busy)return;if(System.currentTimeMillis()-lastFetch<1800000){handler.postDelayed(tick,60000);return;}busy=true;
 new Thread(()->{String headline=null,detail=null;try{
 URL u=new URL("https://api.open-meteo.com/v1/forecast?latitude=47.07855,47.21997,47.1069&longitude=39.55532,39.70769,39.41486&current=temperature_2m,weather_code&timezone=Europe%2FMoscow&forecast_days=1");
 HttpURLConnection c=(HttpURLConnection)u.openConnection();c.setConnectTimeout(12000);c.setReadTimeout(12000);
 String data;try(InputStream in=c.getInputStream();ByteArrayOutputStream b=new ByteArrayOutputStream()){byte[] buf=new byte[4096];int n;while((n=in.read(buf))!=-1)b.write(buf,0,n);data=b.toString(StandardCharsets.UTF_8.name());}finally{c.disconnect();}
 JSONArray arr=new JSONArray(data);JSONObject now=arr.getJSONObject(0).getJSONObject("current");
 headline="Кулешовка  "+temp(now)+"  ·  "+condition(now.getInt("weather_code"));
 detail="Ростов-на-Дону  "+temp(arr.getJSONObject(1).getJSONObject("current"))+"     Азов  "+temp(arr.getJSONObject(2).getJSONObject("current"));
 }catch(Exception e){android.util.Log.w("HomeWeather","Weather unavailable",e);}
 final String h=headline,d=detail;handler.post(()->{busy=false;if(h!=null){lastFetch=System.currentTimeMillis();cache.edit().putString("main",h).putString("other",d).putLong("updated",lastFetch).apply();if(!activity.isFinishing()){main.setText(h);other.setText(d);}}else if(!activity.isFinishing()){long t=cache.getLong("updated",0);other.setText(t>0?cache.getString("other","")+"  ·  нет обновления":"Погода временно недоступна");}if(active)handler.postDelayed(tick,h!=null?1800000:300000);});},"home-weather").start();
 }
 static String temp(JSONObject j)throws JSONException {int t=(int)Math.round(j.getDouble("temperature_2m"));return (t>0?"+":"")+t+"°";}
 static String condition(int c){if(c==0)return "Ясно";if(c<=2)return "Переменная облачность";if(c==3)return "Пасмурно";if(c<=48)return "Туман";if(c<=57)return "Морось";if(c<=67)return "Дождь";if(c<=77)return "Снег";if(c<=82)return "Ливень";if(c<=86)return "Снегопад";return "Гроза";}
}
