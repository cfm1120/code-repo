package com.sbstudio;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kan.entity.Article;
import com.kan.util.http.HttpUtils;
import com.kan.util.http.ReadImgAsyncTask;
import com.sbstudio.LazyScrollView.OnScrollListener;
import com.sbstudio.kan.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
//	private AssetManager assetManager;
	private List<String> image_urls;
//	private final String image_path = "images";

	private int itemWidth;

	private int column_count = 2;// 显示列数
	private int page_count = 5;// 每次加载15张图片

	private int current_page = 0;
	private List<Article> articles;

	private static final int COMPLETE=1;
	
	
	
	
	private Handler myHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE:
                    // 加载所有图片路径
                    image_urls=new ArrayList<String>();
                    for (Article article : articles) {
                        image_urls.add(article.getCoverimg());
                    }
                    //          image_urls = Arrays.asList(assetManager.list(image_path));
                    //test
//                    image_urls = Arrays.asList(new String[]{"http://ww1.sinaimg.cn/mw240/a008c71fjw1e40o4mff67j20go0p0gpx.jpg","http://ww4.sinaimg.cn/mw240/58e1d970jw1e50b2p7bsrj20iw0cidgy.jpg",
//                            "http://ww2.sinaimg.cn/mw240/821e83edtw1e5g59by1avj20m80wa44o.jpg","http://ww1.sinaimg.cn/mw240/806e646djw1e5fsuhfd0pj21hc0zkaid.jpg"});
                    // 第一次加载
                    AddItemToContainer(current_page, page_count);
                    break;
                default:
                    break;
            }
        }
        
    };
	
    /**
     * 读取文章封面页
     */
    public void loadArticleCover(){
        new Thread()
        {
            public void run()
            {
                //界面开始时请求http
                try {
                    articles=HttpUtils.getHttpResponseObjectList("http://www.3sbstudio.com/api/kan.php?type=article&kid=3569497735585857&page=1&count=10", Article[].class);
                    myHandler.sendEmptyMessage(COMPLETE);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		display = this.getWindowManager().getDefaultDisplay();
		itemWidth = display.getWidth() / column_count;// 根据屏幕大小计算每列大小
//		assetManager = this.getAssets();

		InitLayout();
		loadArticleCover();
		

	}

	private void InitLayout() {
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
				// 滚动到最低端
				AddItemToContainer(++current_page, page_count);
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
