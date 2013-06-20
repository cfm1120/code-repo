package com.sbstudio.kan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sbstudio.kan.entity.Article;
import com.sbstudio.kan.util.http.HttpUtils;
import com.sbstudio.kan.view.ScaleImageView;
import com.sbstudio.kan.view.XListView;
import com.sbstudio.kan.view.XListView.IXListViewListener;
import com.sbstudio.kan.view.XListViewFooter;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SampleActivity extends FragmentActivity implements IXListViewListener {
    private XListView xlistView = null;
    private StaggeredAdapter mAdapter = null;
    private int currentPage = 1;

    private LinkedList<Article> mInfos;
    private List<Article> articles;
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    
    private static final int PAGE_COUNT = 10;// 每次加载5张图片
    private static final int COMPLETE=1;
    private static final int COMPLETE_ADD=2;
    private static final int NET_ERROR=-1;
    
    String kid="3569497735585857";
    
    
    /**
     * 用于保存当前滚动的位置
     */
//    private int mPosition ;
    
    private Handler myHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE:
                    // 加载所有文章路径
                    mInfos.clear();
                    for (Article article : articles) {
                        if(null==article.getCoverimg()||article.getCoverimg().equals(""))
                            continue;
                        mInfos.add(article);
                    }
                    mAdapter.notifyDataSetChanged();
                    xlistView.stopLoadMore();
                    xlistView.getFooterView().setState(XListViewFooter.STATE_NORMAL);
//                    AddItemToContainer(current_page, PAGE_COUNT,2);
                    break;
                case COMPLETE_ADD:
                    for (Article article : articles) {
                        mInfos.add(article);
                    }
                    mAdapter.notifyDataSetChanged();
                    xlistView.stopLoadMore();
                    break;
                case NET_ERROR:
                    Toast.makeText(getApplicationContext(), R.string.net_error, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
        
    };
    /**
     * 读取文章封面页
     */
    public void loadArticleCover(final int page,final int count){
        new Thread()
        {
            public void run()
            {
                //界面开始时请求http
                try {
                    articles=HttpUtils.getHttpResponseObjectList("http://www.3sbstudio.com/api/kan.php?type=article&kid="+kid+"&page="+page+"&count="+count, Article[].class);
                    myHandler.sendEmptyMessage(COMPLETE);
                } catch (IOException e) {
                     myHandler.sendEmptyMessage(NET_ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
  
    

    /**
     * 添加内容
     * @param page
     * @param count
     */
    private void AddItemToContainer(final int page,final int count) {
        
        new Thread()
        {
            public void run()
            {
                //界面开始时请求http
                try {
                    articles=HttpUtils.getHttpResponseObjectList("http://www.3sbstudio.com/api/kan.php?type=article&kid="+kid+"&page="+page+"&count="+count, Article[].class);
                    myHandler.sendEmptyMessage(COMPLETE_ADD);
                } catch (IOException e) {
                    myHandler.sendEmptyMessage(NET_ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        
    }

    public class StaggeredAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder=null;

            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
                convertView = layoutInflator.inflate(R.layout.infos_list, null);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            if(holder==null)
            {
                holder = new ViewHolder();
                holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
                convertView.setTag(holder);
            }
            

//            mImageFetcher.loadImage(duitangInfo.getIsrc(), holder.imageView);
            imageLoader.displayImage(mInfos.get(position).getCoverimg(), holder.imageView, options, null);
//            holder.imageView.setBackgroundResource(R.drawable.ic_empty);
            return convertView;
        }

        class ViewHolder {
            ScaleImageView imageView;
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mInfos.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pull_to_refresh_sample);
        
        options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_stub)
        .showImageForEmptyUri(R.drawable.ic_empty)
        .showImageOnFail(R.drawable.ic_error)
        .cacheInMemory()
        .cacheOnDisc()
//        .displayer(new RoundedBitmapDisplayer(20))
        .build();
        
        xlistView = (XListView) findViewById(R.id.list);
        xlistView.setPullLoadEnable(true);
        xlistView.setXListViewListener(this);
        xlistView.setPullRefreshEnable(false);
        xlistView.getFooterView().setState(XListViewFooter.STATE_LOADING);
//        xlistView.setOnScrollListener(new OnScrollListener() {
//            
//            @Override
//            public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
//                // 不滚动时保存当前滚动到的位置
//                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
////                    mPosition=xlistView.getFirstVisiblePosition();
//                 Log.e("YYYYY", xlistView.getScroller().getCurrY()+" "+xlistView.getScroller().getCurrX());
//                }
//                
//            }
//            
//            @Override
//            public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount,
//                    int totalItemCount) {
//                
//            }
//        });
        
        xlistView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), position+":"+mInfos.get(position).getAid(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), GalleryActivity.class);
                intent.putExtra("article", mInfos.get(position-1));
                startActivity(intent);
            }
        });
        
        mInfos=new LinkedList<Article>();
        
        loadArticleCover(1, PAGE_COUNT);
        mAdapter=new StaggeredAdapter();
      
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mImageFetcher.setExitTasksEarly(false);
        xlistView.setAdapter(mAdapter);
//        AddItemToContainer(currentPage,PAGE_COUNT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        //已经setPullRefreshEnable(false)了，这里其实没用
        loadArticleCover(1, PAGE_COUNT);

    }

    @Override
    public void onLoadMore() {
        AddItemToContainer(++currentPage,PAGE_COUNT);

    }
}// end of class
