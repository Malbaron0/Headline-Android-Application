/**
 * PostItemAdapter.java
 * 
 * Adapter Class which configs and returns the View for ListView
 * 
 */
package com.jmsliu.rssreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmsliu.rssreader.GlobalClass;
import com.jmsliu.rssreader.R;
import com.jmsliu.rssreader.model.vo.PostData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostItemAdapter extends ArrayAdapter<PostData> {
	private LayoutInflater inflater;
	private ArrayList<PostData> datas;

	public PostItemAdapter(Context context, int textViewResourceId,
			ArrayList<PostData> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		inflater = ((Activity) context).getLayoutInflater();
		datas = objects;
	}

	static class ViewHolder {
		TextView postTitleView;
		TextView postDateView;
		ImageView postThumbView;
        String postThumbURL;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.postitem, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.postThumbView = (ImageView) convertView
					.findViewById(R.id.postThumb);
			viewHolder.postTitleView = (TextView) convertView
					.findViewById(R.id.postTitleLabel);
			viewHolder.postDateView = (TextView) convertView
					.findViewById(R.id.postDateLabel);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

        //set a default image first
        viewHolder.postThumbView.setImageResource(R.drawable.postthumb_loading);

        PostData post = datas.get(position);
        if (post.postThumbUrl == null && post.postDescription != null) {
            //search <img> tag in description
            Pattern p = Pattern.compile(".*?(<img [^>]*/?>).*", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(post.postDescription);
            if(m.matches()) {
                String imgString = m.group(1);
                Pattern sourcePattern = Pattern.compile(".*src=[\"']{1}(http[^\"]*)[\"']{1}.*");
                m = sourcePattern.matcher(imgString);
                if(m.matches()) {
                    post.postThumbUrl = m.group(1);
                }
            }
        }

        if(post.postThumbUrl != null) {
            viewHolder.postThumbURL = post.postThumbUrl;
            new DownloadImageTask().execute(viewHolder);
        }

		viewHolder.postTitleView.setText(post.postTitle);
		viewHolder.postDateView.setText(post.postDate);
		return convertView;
	}

    private class DownloadImageTask extends
            AsyncTask<ViewHolder, Integer, ViewHolder> {

        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder = params[0];
            String urlStr = viewHolder.postThumbURL;
            String localFileName = Integer.toString(urlStr.hashCode());

            try {
                File cacheDir = GlobalClass.instance().getCacheFolder(PostItemAdapter.this.getContext());
                File cacheFile = new File(cacheDir, localFileName);
                if (!cacheFile.exists()) {
                    URL url = new URL(urlStr);
                    //URLConnection connection = url.openConnection();
                    InputStream inputStream = new BufferedInputStream(
                            url.openStream(), 10240);
                    FileOutputStream outputStream = new FileOutputStream(
                            cacheFile);

                    byte buffer[] = new byte[1024];
                    int dataSize;
                    int loadedSize = 0;
                    while ((dataSize = inputStream.read(buffer)) != -1) {
                        loadedSize += dataSize;
                        publishProgress(loadedSize);
                        outputStream.write(buffer, 0, dataSize);
                    }

                    outputStream.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return viewHolder;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(ViewHolder result) {
            String urlStr = result.postThumbURL;
            String imagePath = Integer.toString(urlStr.hashCode());

            //read file from local
            InputStream fileInputStream;
            try {
                File cacheDir = GlobalClass.instance().getCacheFolder(PostItemAdapter.this.getContext());
                File cacheFile = new File(cacheDir, imagePath);
                fileInputStream = new FileInputStream(cacheFile);
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);

                //Loading Large Bitmaps Efficiently
                //int imageWidth = bitmapOptions.outWidth;
                int imageHeight = bitmapOptions.outHeight;

                //decode the image with necessary size
                int scale = imageHeight / R.dimen.thumbnail_size;
                fileInputStream = new FileInputStream(cacheFile);
                bitmapOptions.inSampleSize = scale;
                bitmapOptions.inJustDecodeBounds = false;
                result.postThumbView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
