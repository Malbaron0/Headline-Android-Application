package com.jmsliu.rssreader;

import android.content.Context;
import android.os.Environment;

import com.jmsliu.rssreader.model.vo.DrawerData;

import java.io.File;
import java.util.ArrayList;


public class GlobalClass {
	private	static GlobalClass instance;
    public static final String RSSURL = "http://www.theguardian.com/us/rss";
    public static final String RSSURL1 = "http://www.dailymail.co.uk/articles.rss";
    public static final String CATEGORY_RSSURL = "http://www.theguardian.com/%s/rss";
    public static final String CATEGORY_RSSURL1 = "http://www.dailymail.co.uk/%s/index.rss";
    public static final String SINGLE_POSTURL = "http://www.theguardian.com/us/rss";
	private static final String applicationCacheFolder = "wallpaper_jms/cache/";
	private static final String applicationPicFolder = "wallpaper_jms/data/";
    public ArrayList<DrawerData> categoryList;
	
	public static GlobalClass instance() {
		if(instance == null) {
			instance = new GlobalClass();
            instance.categoryList = new ArrayList<DrawerData>();
            DrawerData data = new DrawerData();
            data.title = "Category";
            data.name = "";
            data.name1 = "";
            data.type = 1;
            instance.categoryList.add(data);
            data = new DrawerData();
            data.title = "All";
            data.name = "";
            data.name1 = "";
            data.type = 0;
            instance.categoryList.add(data);
            data = new DrawerData();
            data.title = "Sports";
            data.name = "sport";
            data.name1 = "sport";
            data.type = 0;
            instance.categoryList.add(data);
            data = new DrawerData();
            data.title = "Travel";
            data.name = "travel";
            data.name1 = "travel";
            data.type = 0;
            instance.categoryList.add(data);
            data = new DrawerData();
            data.title = "Technology";
            data.name = "technology";
            data.name1 = "sciencetech";
            data.type = 0;
            instance.categoryList.add(data);
            data = new DrawerData();
            data.title = "Business";
            data.name = "business";
            data.name1 = "money";
            data.type = 0;
            instance.categoryList.add(data);
            data = new DrawerData();
            data.title = "Environment/Health";
            data.name = "environment";
            data.name1 = "health";
            data.type = 0;
            instance.categoryList.add(data);
		}
		
		return instance;
	}
	
	public File getCacheFolder(Context context) {
		File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), applicationCacheFolder);
            if(!cacheDir.isDirectory()) {
            	cacheDir.mkdirs();
            }
        }
        
        if(!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir(); //get system cache folder
        }
        
		return cacheDir;
	}
	
	public File getDataFolder(Context context) {
		File dataDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        	dataDir = new File(Environment.getExternalStorageDirectory(), applicationPicFolder);
            if(!dataDir.isDirectory()) {
            	dataDir.mkdirs();
            }
        }
        
        if(!dataDir.isDirectory()) {
        	dataDir = context.getFilesDir();
        }
        
		return dataDir;
	}
}
