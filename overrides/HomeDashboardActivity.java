package com.gathin.tvhome;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.webkit.*;
import android.widget.*;
public final class HomeDashboardActivity extends Activity {
 private WebView web;
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
  LinearLayout layout=new LinearLayout(this);layout.setOrientation(LinearLayout.VERTICAL);layout.setBackgroundColor(0xFF09111D);
  Button back=new Button(this);back.setText("‹  Главный экран");back.setAllCaps(false);back.setOnClickListener(v->finish());layout.addView(back,new LinearLayout.LayoutParams(-1,Math.round(44*getResources().getDisplayMetrics().density)));
  web=new WebView(this);web.setBackgroundColor(Color.BLACK);WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setAllowFileAccess(false);settings.setAllowContentAccess(false);settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);settings.setSupportZoom(true);settings.setBuiltInZoomControls(true);settings.setDisplayZoomControls(false);
  web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest request){return !"192.168.92.142".equals(request.getUrl().getHost());}});
  layout.addView(web,new LinearLayout.LayoutParams(-1,0,1));setContentView(layout);web.loadUrl("http://192.168.92.142:8123/tv-dashboard/main");web.requestFocus();
 }
 @Override public boolean onKeyDown(int key,KeyEvent event){if(key==KeyEvent.KEYCODE_BACK){finish();return true;}return super.onKeyDown(key,event);}
 @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}
