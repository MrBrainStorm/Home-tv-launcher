package com.gathin.tvhome;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import java.util.*;
final class HomeAppsPage {
 private final Activity activity; private final FrameLayout root; private final FrameLayout page; private final Runnable close;
 private final Set<String> pinned=new HashSet<>(Arrays.asList("watch.kino","com.google.android.youtube.tv","ru.kinopoisk.tv","com.vk.tv","com.gathin.tvhome","com.google.android.tvlauncher"));
 HomeAppsPage(Activity a,FrameLayout r,Runnable done){activity=a;root=r;close=done;page=new FrameLayout(a);}
 int dp(int n){return Math.round(n*activity.getResources().getDisplayMetrics().density);}
 void show(){
  FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(-1,-1);p.topMargin=dp(150);p.bottomMargin=dp(28);p.leftMargin=dp(56);p.rightMargin=dp(56);
  page.setBackgroundColor(0xD908101D);page.setPadding(dp(12),dp(10),dp(12),dp(10));root.addView(page,p);
  GridView grid=new GridView(activity);grid.setNumColumns(6);grid.setVerticalSpacing(dp(14));grid.setHorizontalSpacing(dp(12));grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);page.addView(grid,new FrameLayout.LayoutParams(-1,-1));
  new Thread(()->{
   PackageManager pm=activity.getPackageManager();Map<String,ResolveInfo> found=new HashMap<>();
   for(String category:Arrays.asList(Intent.CATEGORY_LAUNCHER,Intent.CATEGORY_LEANBACK_LAUNCHER))for(ResolveInfo item:pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(category),0))if(!pinned.contains(item.activityInfo.packageName))found.put(item.activityInfo.packageName,item);
   List<ResolveInfo> apps=new ArrayList<>(found.values());apps.sort((a,b)->String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(),b.loadLabel(pm).toString()));
   activity.runOnUiThread(()->{if(page.getParent()==null)return;grid.setAdapter(new BaseAdapter(){
    public int getCount(){return apps.size();}public Object getItem(int i){return apps.get(i);}public long getItemId(int i){return i;}
    public View getView(int i,View reused,android.view.ViewGroup parent){ResolveInfo app=apps.get(i);TextView tile=new TextView(activity);tile.setText(app.loadLabel(pm));tile.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));tile.setTextSize(14);tile.setTextColor(Color.WHITE);tile.setGravity(Gravity.CENTER);tile.setMaxLines(2);tile.setPadding(dp(6),dp(12),dp(6),dp(8));tile.setCompoundDrawablePadding(dp(8));Drawable icon=app.loadIcon(pm);icon.setBounds(0,0,dp(50),dp(50));tile.setCompoundDrawables(null,icon,null,null);tile.setLayoutParams(new AbsListView.LayoutParams(-1,dp(112)));tile.setBackgroundResource(R.drawable.pill_bg);tile.setContentDescription(app.loadLabel(pm));return tile;}
   });grid.setOnItemClickListener((parent,view,pos,id)->{ResolveInfo app=apps.get(pos);try{activity.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(new ComponentName(app.activityInfo.packageName,app.activityInfo.name)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}catch(Exception e){Toast.makeText(activity,"Не удалось открыть приложение",Toast.LENGTH_SHORT).show();}});
   grid.setOnKeyListener((v,key,event)->{if(key==KeyEvent.KEYCODE_DPAD_UP&&grid.getSelectedItemPosition()<6){if(event.getAction()==KeyEvent.ACTION_DOWN)close.run();return true;}return false;});grid.requestFocus();if(!apps.isEmpty())grid.setSelection(0);
   });
  },"second-page-apps").start();
 }
 void remove(){root.removeView(page);}
}
