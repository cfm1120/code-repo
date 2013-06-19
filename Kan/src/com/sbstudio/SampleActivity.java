//package com.sbstudio;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.sbstudio.kan.R;
//import com.sbstudio.kan.entity.Article;
//import com.sbstudio.kan.view.ScaleImageView;
//import com.sbstudio.kan.view.XListView;
//import com.sbstudio.kan.view.XListView.IXListViewListener;
//
//import java.util.LinkedList;
//import java.util.List;
//
//public class SampleActivity extends FragmentActivity implements IXListViewListener {
//    private XListView mAdapterView = null;
//    private StaggeredAdapter mAdapter = null;
//    private int currentPage = 0;
//
//    
//    DisplayImageOptions options;
// 
//
//    /**
//     * 添加内容
//     * 
//     * @param pageindex
//     * @param type
//     *            1为下拉刷新 2为加载更多
//     */
//    private void AddItemToContainer(int pageindex, int type) {
//        
//            String url = "http://www.duitang.com/album/1733789/masn/p/" + pageindex + "/24/";
//            Log.d("MainActivity", "current url:" + url);
//
//
//        
//    }
//
//    public class StaggeredAdapter extends BaseAdapter {
//        private Context mContext;
//        private LinkedList<Article> mInfos;
//        private XListView mListView;
//
//        public StaggeredAdapter(Context context, XListView xListView) {
//            mContext = context;
//            mInfos = new LinkedList<Article>();
//            mListView = xListView;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder holder;
//            Article duitangInfo = mInfos.get(position);
//
//            if (convertView == null) {
//                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
//                convertView = layoutInflator.inflate(R.layout.infos_list, null);
//                holder = new ViewHolder();
//                holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
////                holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
//                convertView.setTag(holder);
//            }
//
//            holder = (ViewHolder) convertView.getTag();
//            holder.imageView.setImageWidth(duitangInfo.getWidth());
//            holder.imageView.setImageHeight(duitangInfo.getHeight());
////            holder.contentView.setText(duitangInfo.getMsg());
//            mImageFetcher.loadImage(duitangInfo.getIsrc(), holder.imageView);
//            return convertView;
//        }
//
//        class ViewHolder {
//            ScaleImageView imageView;
////            TextView contentView;
////            TextView timeView;
//        }
//
//        @Override
//        public int getCount() {
//            return mInfos.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            return mInfos.get(arg0);
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            return 0;
//        }
//
//        public void addItemLast(List<DuitangInfo> datas) {
//            mInfos.addAll(datas);
//        }
//
//        public void addItemTop(List<DuitangInfo> datas) {
//            for (DuitangInfo info : datas) {
//                mInfos.addFirst(info);
//            }
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_pull_to_refresh_sample);
//        
//        options = new DisplayImageOptions.Builder()
//        .showStubImage(R.drawable.ic_stub)
//        .showImageForEmptyUri(R.drawable.ic_empty)
//        .showImageOnFail(R.drawable.ic_error)
//        .cacheInMemory()
//        .cacheOnDisc()
//        .displayer(new RoundedBitmapDisplayer(20))
//        .build();
//        
//        
//        mAdapterView = (XListView) findViewById(R.id.list);
//        mAdapterView.setPullLoadEnable(true);
//        mAdapterView.setXListViewListener(this);
//
//        mAdapter = new StaggeredAdapter(this, mAdapterView);
//
//        mImageFetcher = new ImageFetcher(this, 240);
//        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        return true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mImageFetcher.setExitTasksEarly(false);
//        mAdapterView.setAdapter(mAdapter);
//        AddItemToContainer(currentPage, 2);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }
//
//    @Override
//    public void onRefresh() {
//        AddItemToContainer(++currentPage, 1);
//
//    }
//
//    @Override
//    public void onLoadMore() {
//        AddItemToContainer(++currentPage, 2);
//
//    }
//}// end of class
