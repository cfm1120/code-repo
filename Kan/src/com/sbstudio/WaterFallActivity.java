package com.sbstudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sbstudio.kan.R;
import com.sbstudio.kan.entity.Article;
import com.sbstudio.kan.util.http.HttpUtils;
import com.sbstudio.kan.util.http.ReadImgAsyncTask;
import com.sbstudio.kan.view.LazyScrollView;
import com.sbstudio.kan.view.LazyScrollView.OnScrollListener;

public class WaterFallActivity extends Activity {

	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
//	private AssetManager assetManager;
	private List<String> image_urls;
//	private final String image_path = "images";

	private int itemWidth;

	private int column_count = 2;// 显示列数
	private static final int PAGE_COUNT = 5;// 每次加载15张图片

	private int current_page = 0;
	
	private List<Article> articles;

	private static final int COMPLETE=1;
	private static final int NET_ERROR=-1;
	private String kid;
	
	
	private RelativeLayout loadingView;
	private ProgressBar loadingProgressBar;
	private TextView loadingTextView;
	
	private Handler myHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE:
                    // 加载所有图片路径
                    for (Article article : articles) {
                        image_urls.add(article.getCoverimg());
                    }
                    AddItemToContainer(current_page, PAGE_COUNT);
                    loadingView.setVisibility(View.GONE);
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
                    current_page=page-1;
                    myHandler.sendEmptyMessage(COMPLETE);
                } catch (IOException e) {
                	 myHandler.sendEmptyMessage(NET_ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waterfall);
		display = this.getWindowManager().getDefaultDisplay();
		itemWidth = display.getWidth() / column_count;// 根据屏幕大小计算每列大小

		InitLayout();
		
		
		Intent intent=getIntent();
		kid=intent.getStringExtra("kid");
		
		image_urls=new ArrayList<String>();
		loadArticleCover(current_page+1,PAGE_COUNT);

	}

	private void InitLayout() {
		//添加listview底部获取更多按钮（可自定义）,把这个作为加载ing的提示
		loadingView =(RelativeLayout)findViewById(R.id.loading);
		loadingTextView=(TextView) loadingView.findViewById(R.id.text_view);
		loadingTextView.setText("正在加载");
		loadingProgressBar=(ProgressBar) loadingView.findViewById(R.id.footer_progress);
		loadingProgressBar.setVisibility(View.VISIBLE);
		
		
		
		
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {
				// 滚动中
				Log.d("LazyScroll", "Scroll");
			}

			@Override
			public void onBottom() {
				loadingView.setVisibility(View.VISIBLE);
				// 滚动到最低端
				loadArticleCover(++current_page+1, PAGE_COUNT);
//				AddItemToContainer(++current_page, PAGE_COUNT);
			}
		});

		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		waterfall_items = new ArrayList<LinearLayout>();

		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					itemWidth, LayoutParams.WRAP_CONTENT);
			// itemParam.width = itemWidth;
			// itemParam.height = LayoutParams.WRAP_CONTENT;
			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
			
		}


	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		int j = 0;
		int imagecount = image_urls.size();
		for (int i = pageindex * pagecount; i < pagecount * (pageindex + 1)
				&& i < imagecount; i++) {
			j = j >= column_count ? j = 0 : j;
			AddImage(image_urls.get(i), j++);

		}

	}

	private void AddImage(String url, int columnIndex) {
		ImageView item = (ImageView) LayoutInflater.from(this).inflate(
				R.layout.waterfallitem, null);
		waterfall_items.get(columnIndex).addView(item);

		
		for (Article article : articles) {
			if(article.getCoverimg().equals(url))
			{
				item.setTag(article);
				break;
			}
		}
		
		item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(MainActivity.this, v.getTag()+"", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(WaterFallActivity.this, GalleryActivity.class);
				intent.putExtra("article", (Article)v.getTag());
				startActivity(intent);
				
			}
		});
//		TaskParam param = new TaskParam();
//		param.setAssetManager(assetManager);
//		param.setFilename(image_path + "/" + url);
//		param.setItemWidth(itemWidth);
//		ImageLoaderTask task = new ImageLoaderTask(item);
//		task.execute(param);
		
		//test
		ReadImgAsyncTask.load(item, url, itemWidth, 900);

	}
}
