package com.baron.rssreader.model;

import android.os.AsyncTask;
import android.util.Log;

import com.baron.rssreader.model.vo.PostData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class PostDataModel {
    private static PostDataModel instance = null;
    private PostDataDelegate delegate;
    public ArrayList<String> guidList;
    public ArrayList<PostData> listData;
    private boolean isRefreshLoading = true;

    private enum RSSXMLTag {
        TITLE, DATE, LINK, CONTENT, GUID, IGNORETAG, FEATURED_IMAGE, DESCRIPTION;
    }
    private RSSXMLTag currentTag;

    protected PostDataModel() {
        listData = new ArrayList<PostData>();
        guidList = new ArrayList<String>();
    }

    public static PostDataModel getInstance() {
        if(instance == null) {
            instance = new PostDataModel();
        }

        return instance;
    }

    public void setDelegate(PostDataDelegate _delegate) {
        this.delegate = _delegate;
    }

    public void loadData(String _url,  boolean _isLatest) {
        isRefreshLoading = _isLatest;
        new LoadPostTask().execute(_url);

    }

    private class LoadPostTask extends AsyncTask<String, Integer, ArrayList<PostData>> {
        @Override
        protected ArrayList<PostData> doInBackground(String... params) {
            // TODO Auto-generated method stub
            String urlStr = params[0];
            InputStream is = null;
            ArrayList<PostData> postDataList = new ArrayList<PostData>();

            URL url;
            try {
                url = new URL(urlStr);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                //connection.setReadTimeout(10 * 1000);
                //connection.setConnectTimeout(10 * 1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                Log.d("debug", "The response is: " + response);
                is = connection.getInputStream();

                // parse xml
                XmlPullParserFactory factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                PostData pdData = null;
                SimpleDateFormat inDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                inDateFormat.setLenient(false);
                SimpleDateFormat outDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {

                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("item")) {
                            pdData = new PostData();
                            currentTag = RSSXMLTag.IGNORETAG;
                        } else if (xpp.getName().equals("title")) {
                            if(xpp.getPrefix() == null) {
                                currentTag = RSSXMLTag.TITLE;
                            } else {
                                currentTag = RSSXMLTag.IGNORETAG;
                            }
                        } else if (xpp.getName().equals("link")) {
                            currentTag = RSSXMLTag.LINK;
                        } else if (xpp.getName().equals("pubDate")) {
                            currentTag = RSSXMLTag.DATE;
                        } else if (xpp.getName().equals("encoded")) {
                            currentTag = RSSXMLTag.CONTENT;
                        } else if (xpp.getName().equals("guid")) {
                            currentTag = RSSXMLTag.GUID;
                        } else if (xpp.getName().equals("description")) {
                            currentTag = RSSXMLTag.DESCRIPTION;
                        } else if (xpp.getName().equals("jms-featured-image")) {
                            currentTag = RSSXMLTag.FEATURED_IMAGE;
                            //pdData.postThumbUrl = xpp.getAttributeValue(null, "url"); //read attribute in tags
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equals("item")) {
                            // format the data here, otherwise format data in adapter
                            try {
                                Date postDate = inDateFormat.parse(pdData.postDate);
                                pdData.postDate = outDateFormat.format(postDate);
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                // dateFormat.parse(pdData.postDate);
                                //googleTracker.sendEvent("debug", "ParseException", e.toString(), null);
                                e.printStackTrace();
                            }

                            postDataList.add(pdData);
                        } else {
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();
                        content = content.trim();
                        if (pdData != null) {
                            switch (currentTag) {
                                case TITLE:
                                    if (content.length() != 0) {
                                        if (pdData.postTitle != null) {
                                            pdData.postTitle += content;
                                        } else {
                                            pdData.postTitle = content;
                                        }
                                    }
                                    break;
                                case LINK:
                                    if (content.length() != 0) {
                                        if (pdData.postLink != null) {
                                            pdData.postLink += content;
                                        } else {
                                            pdData.postLink = content;
                                        }
                                    }
                                    break;
                                case DATE:
                                    if (content.length() != 0) {
                                        if (pdData.postDate != null) {
                                            pdData.postDate += content;
                                        } else {
                                            pdData.postDate = content;
                                        }
                                    }
                                    break;
                                case CONTENT:
                                    if (content.length() != 0) {
                                        if (pdData.postContent != null) {
                                            pdData.postContent += content;
                                        } else {
                                            pdData.postContent = content;
                                        }
                                    }
                                    break;
                                case GUID:
                                    if (content.length() != 0) {
                                        if (pdData.postGuid != null) {
                                            pdData.postGuid += content;
                                        } else {
                                            pdData.postGuid = content;
                                        }
                                    }
                                    break;
                                case FEATURED_IMAGE:
                                    if (content.length() != 0) {
                                        if (pdData.postThumbUrl != null) {
                                            pdData.postThumbUrl += content;
                                        } else {
                                            pdData.postThumbUrl = content;
                                        }
                                    }
                                case DESCRIPTION:
                                    try {
                                        if (content.length() != 0) {
                                            if (pdData.postDescription != null) {
                                                pdData.postDescription += content;
                                            } else {
                                                pdData.postDescription = content;
                                            }
                                        }
                                    }
                                    catch (Exception ex){
                                        Log.d("debug",ex.getMessage());
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    eventType = xpp.next();
                }
                Log.v("tst", String.valueOf(postDataList.size()));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                // new URL exception
                //googleTracker.sendEvent("debug", "MalformedURLException", e.toString(), null);
                e.printStackTrace();
            } catch (ProtocolException e) {
                // TODO Auto-generated catch block
                // setRequestMethod exception
                //googleTracker.sendEvent("debug", "ProtocolException", e.toString(), null);
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                // XmlPullParserFactory.newInstance()
                //googleTracker.sendEvent("debug", "XmlPullParserException", e.toString(), null);
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // openConnection()
                // connection.getResponseCode()
                // connection.connect();
                // connection.getInputStream()
                // xpp.next()
                //googleTracker.sendEvent("debug", "IOException", e.toString(), null);
                e.printStackTrace();
            }
            return postDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostData> result) {
            // TODO Auto-generated method stub
            boolean isUpdated = false;
            int j = 0;
            for (int i = 0; i < 10; i++) {
                // check if the post is already in the list
                if (guidList.contains(result.get(i).postLink)) {
                    continue;
                } else {
                    isUpdated = true;
                    guidList.add(result.get(i).postLink);

                    if (isRefreshLoading) {
                        listData.add(j, result.get(i));
                        j++;
                    } else {
                        listData.add(result.get(i));
                    }
                }
            }

            Collections.shuffle(listData);
            delegate.onDataLoadComplete(isUpdated);
            super.onPostExecute(result);
        }
    }
}
