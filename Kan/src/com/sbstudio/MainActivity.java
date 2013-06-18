package com.sbstudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sbstudio.kan.R;
import com.sbstudio.kan.entity.Kan;
import com.sbstudio.kan.util.http.HttpUtils;
import com.sbstudio.kan.util.http.ReadImgAsyncTask;
import com.sbstudio.kan.view.RTPullListView;
import com.sbstudio.kan.view.RTPullListView.OnRefreshListener;

/**
 * PullListView
 * @author Ryan
 *
 */
public class MainActivity extends Activity {
	private static final int NET_ERROR = -1;
	private static final int LOAD_NEW_INFO = 1;
	private static final int LOAD_MORE_NODATA = 2;
	private static final int LOAD_MORE_SUCCESS = 3;
	

	/** 每页加载几张 **/
	private static final int PAGE_COUNT = 2;
	/** 当前加载到第几页  **/
	private int PAGE_NOW = 1;
	private LayoutInflater inflater;
	private RelativeLayout footerView;
	private View footerviewWrapped;
	private TextView loadMoreTextView;
	
	private RTPullListView pullListView;
	private ProgressBar moreProgressBar;
	
	private MyAdapter adapter;
	private List<Kan> kans;
	private List<Map<String, Object>> dataList;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化一些变量
        kans=new ArrayList<Kan>();
        dataList = new ArrayList<Map<String,Object>>();
        //test
//        for(int i=0;i<3;i++)
//        {
//        	Kan kan=new Kan();
//        	kan.setKid("3573482999288822");
//        	kan.setImg("http://ww4.sinaimg.cn/mw240/58e1d970jw1e50b2p7bsrj20iw0cidgy.jpg");
//        	kan.setName("美女之都"+i);
//        	kan.setLasttime("1369446809");
//        	kans.add(kan);
//        }
        //test done
      
        
        
        
        pullListView = (RTPullListView) this.findViewById(R.id.pullListView);
        
        adapter=new MyAdapter(getApplicationContext());
		pullListView.setAdapter(adapter);
		
		//添加listview底部获取更多按钮（可自定义）
        inflater = LayoutInflater.from(this);
		footerviewWrapped = inflater.inflate(R.layout.list_footview, null);
		footerView =(RelativeLayout) footerviewWrapped.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) footerviewWrapped.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);
		loadMoreTextView=(TextView) footerView.findViewById(R.id.text_view);
		//下拉刷新监听器
		pullListView.setonRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				pullRefresh();
			}

			private void pullRefresh() {

				new Thread(new Runnable() {
					
					@Override
					public void run() {
						
							try {
								PAGE_NOW=1;
								kans=HttpUtils.getHttpResponseObjectList("http://www.3sbstudio.com/api/kan.php?type=kan&page="+PAGE_NOW+"&count="+PAGE_COUNT, Kan[].class);
								dataList.clear();
								for (Kan kan : kans) {
							    	Map<String, Object> map = new HashMap<String, Object>();
									map.put("kid", kan.getKid());
									map.put("img", kan.getImg());
									map.put("name", kan.getName());
									dataList.add(map);
								}
								myHandler.sendEmptyMessage(LOAD_NEW_INFO);
							} catch (IOException e1) {
								myHandler.sendEmptyMessage(NET_ERROR);
								e1.printStackTrace();
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}
						
					
				}).start();
				
			}
		});
		pullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Intent intent=new Intent(getApplicationContext(),WaterFallActivity.class);
				intent.putExtra("kid", dataList.get(position-1).get("kid")+"");
				startActivity(intent);
			}
		});
		
		//获取更多监听器
		footerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				moreProgressBar.setVisibility(View.VISIBLE);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
	                    try {
							kans=HttpUtils.getHttpResponseObjectList("http://www.3sbstudio.com/api/kan.php?type=kan&page="+ ++PAGE_NOW+"&count="+PAGE_COUNT, Kan[].class);
							for (Kan kan : kans) {
						    	Map<String, Object> map = new HashMap<String, Object>();
								map.put("kid", kan.getKid());
								map.put("img", kan.getImg());
								map.put("name", kan.getName());
								dataList.add(map);
							}
							myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
	                    } catch (IOException e1) {
	                    	myHandler.sendEmptyMessage(NET_ERROR);
						} catch (JSONException e1) {
							myHandler.sendEmptyMessage(LOAD_MORE_NODATA);
						}
					}
				}).start();
			}
		});
		
		//第一次进入
		 loadMoreTextView.setText("正在加载微刊...");
	     moreProgressBar.setVisibility(View.VISIBLE);
	     footerView.setClickable(false);
	     loadKan(PAGE_NOW, PAGE_COUNT);
		
		
    }
    
    //结果处理
    private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_MORE_SUCCESS:
				moreProgressBar.setVisibility(View.GONE);
//				addDataList();
				adapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				break;
			case LOAD_MORE_NODATA:
				moreProgressBar.setVisibility(View.GONE);
				loadMoreTextView.setText("没有更多了...");
				footerView.setClickable(false);
				break;
			case LOAD_NEW_INFO:
				adapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				loadMoreTextView.setText("获取更多");
				footerView.setClickable(true);
				moreProgressBar.setVisibility(View.GONE);
				break;
			case NET_ERROR:
				Toast.makeText(getApplicationContext(), R.string.net_error, Toast.LENGTH_SHORT).show();
//			case COMPLETE:
//				 adapter.notifyDataSetChanged();
//				 pullListView.onRefreshComplete();
//				 moreProgressBar.setVisibility(View.GONE);
//				 loadMoreTextView.setText("获取更多");
//				 footerView.setClickable(true);
//				 break;
			default:
				break;
			}
		}

    };
    
    /**
     * 读取微刊
     * @param page
     * @param count
     */
    public synchronized void loadKan(final int page,final int count){
        new Thread()
        {
            public void run()
            {
                //界面开始时请求http
                try {
                    kans=HttpUtils.getHttpResponseObjectList("http://www.3sbstudio.com/api/kan.php?type=kan&page="+page+"&count="+count, Kan[].class);
                    for (Kan kan : kans) {
    			    	Map<String, Object> map = new HashMap<String, Object>();
    					map.put("kid", kan.getKid());
    					map.put("img", kan.getImg());
    					map.put("name", kan.getName());
    					dataList.add(map);
    				}
                    myHandler.sendEmptyMessage(LOAD_NEW_INFO);
                } catch (IOException e) {
                    myHandler.sendEmptyMessage(NET_ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    
    /**
     * 自定义adapter
     * @author water3
     *
     * 2013-6-14
     */
	class MyAdapter extends BaseAdapter {

		public MyAdapter(Context context) {
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//使用convertView和ViewHolder提升listview性能
			ViewHolder holder = null;
			if(convertView == null){
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.listitem, null);
			}
			else
			{
				holder  = (ViewHolder) convertView.getTag() ;
			}
			 if(holder==null){
				 holder = new ViewHolder();
				 holder.kan_img=(ImageView) convertView.findViewById(R.id.kan_img);
				 holder.kan_name = (TextView) convertView.findViewById(R.id.kan_name);
				
				 convertView.setTag(holder) ;
			 }
			
			
			//监听事件里面用到的
//			final String kanId=(String) dataList.get(position).get("kid");
			
			ReadImgAsyncTask.load(holder.kan_img, (String)dataList.get(position).get("img"), 240, 160);
			holder.kan_name.setText((String)dataList.get(position).get("name"));
			return convertView;
		}

	}
   static class ViewHolder{
		 ImageView kan_img;
		 TextView kan_name;
   }
   

}