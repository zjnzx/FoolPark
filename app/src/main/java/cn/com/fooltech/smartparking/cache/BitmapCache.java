package cn.com.fooltech.smartparking.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * 缓存类
 * @author Administrator
 *
 */
public class BitmapCache implements ImageCache {
	private LruCache<String, Bitmap> cache;
	private int maxSize = 10 * 1024 * 1024;
	
	public BitmapCache(){
		cache = new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		
	}
	@Override
	public Bitmap getBitmap(String url) {
		return cache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		cache.put(url, bitmap);
	}
}
