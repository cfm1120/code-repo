/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.sbstudio.kan;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sbstudio.kan.R;
import com.sbstudio.kan.entity.Article;

public class GalleryActivity extends Activity {

    private GalleryViewPager mViewPager;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        Intent intent=getIntent();
        Article article=(Article) intent.getSerializableExtra("article");
//        Log.d("11", article.getAid());
        
//        String[] urls = {
//                "http://cs407831.userapi.com/v407831207/18f6/jBaVZFDhXRA.jpg",
//                "http://cs407831.userapi.com/v4078f31207/18fe/4Tz8av5Hlvo.jpg",
//                "http://cs407831.userapi.com/v407831207/1906/oxoP6URjFtA.jpg",
//                "http://cs407831.userapi.com/v407831207/190e/2Sz9A774hUc.jpg",
//                "http://cs407831.userapi.com/v407831207/1916/Ua52RjnKqjk.jpg",
//                "http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg"
//        };
//        List<String> items = new ArrayList<String>();
//        Collections.addAll(items, urls);

//        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
        UrlPagerAdapter pagerAdapter=null;
        if(null!=article)
         pagerAdapter = new UrlPagerAdapter(this, article.getContent());
/*        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
		{
			@Override
			public void onItemChange(int currentPosition)
			{
				Toast.makeText(GalleryUrlActivity.this, "Current item is " + currentPosition, Toast.LENGTH_SHORT).show();
			}
		});*/
        
        mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        
    }

}