package com.baron.rssreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baron.rssreader.adapter.PostItemAdapter;
import com.baron.rssreader.component.RefreshableInterface;
import com.baron.rssreader.component.RefreshableListView;
import com.baron.rssreader.model.PostDataDelegate;
import com.baron.rssreader.model.PostDataModel;

public class PostListFragment extends Fragment
        implements PostDataDelegate, RefreshableInterface {
    private RefreshableListView postListView;
    private PostItemAdapter postAdapter;
    private boolean isLoading = false;
    private boolean isRefreshLoading = false;
    private int page = 1;
    public String rssURL;
    public String rssURL1;
    private PostListFragmentInteractionListener mListener;

    public static PostListFragment getInstance(String aURL) {
        PostListFragment fragment = new PostListFragment();
        fragment.rssURL = aURL;
        return fragment;
    }

    public PostListFragment() {
        //default url
        this.rssURL = GlobalClass.RSSURL;
        this.rssURL1 = GlobalClass.RSSURL1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_postlist, container, false);

        postListView = (RefreshableListView) rootView.findViewById(R.id.postListView);
        postAdapter = new PostItemAdapter(this.getActivity(), R.layout.postitem, PostDataModel.getInstance().listData);
        postListView.setAdapter(postAdapter);
        postListView.setOnRefresh(this);
        postListView.setOnItemClickListener(onItemClickListener);
        PostDataModel.getInstance().setDelegate(this);
        return rootView;
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //PostData data = PostDataModel.getInstance().listData.get(arg2 - 1);
            // Send the event and Uri to the host activity
            mListener.onPostSelected(arg2 - 1);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PostListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }

    }

    @Override
    public void onDataLoadComplete(boolean _isUpdated) {
        if (_isUpdated) {
            postAdapter.notifyDataSetChanged();
        }

        if (isRefreshLoading) {
            postListView.onRefreshComplete();
        } else {
            postListView.onLoadingMoreComplete();
        }

        isLoading = false;
    }

    @Override
    public void startFresh() {
        if (!isLoading) {
            isRefreshLoading = true;
            isLoading = true;

			/*
             * Pagination:
			 *
			 * If your rss feed looks like:
			 *
			 * "http://jmsliu.com/feed?paged="
			 *
			 * You can try follow code for pagination.
			 *
			 * new RssDataController().execute(urlString + 1);
			 */
            PostDataModel.getInstance().loadData(rssURL, isRefreshLoading);
            PostDataModel.getInstance().loadData(rssURL1, isRefreshLoading);
        } else {
            postListView.onRefreshComplete();
        }
    }

    @Override
    public void startLoadMore() {
        if (!isLoading) {
            isRefreshLoading = false;
            isLoading = true;
			/*
			 * Pagination:
			 *
			 * If your rss feed source looks like "http://jmsliu.com/feed?paged=",
			 * you can try follow code for pagination:
			 *
			 * new RssDataController().execute(urlString + (++pagnation));
			 *
			 * Otherwise, please use this:
			 *
			 * new RssDataController().execute(urlString);
			 */
            PostDataModel.getInstance().loadData(rssURL + (++page), isRefreshLoading);
            PostDataModel.getInstance().loadData(rssURL1 + (++page), isRefreshLoading);
        } else {
            postListView.onLoadingMoreComplete();
        }
    }

    public void manualRefresh() {
        postListView.onRefreshStart();
    }

    public interface PostListFragmentInteractionListener {
        public void onPostSelected (int index);
    }
}
