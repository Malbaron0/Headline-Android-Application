package com.baron.rssreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.baron.rssreader.model.PostDataModel;
import com.baron.rssreader.model.vo.DrawerData;
import com.baron.rssreader.model.vo.PostData;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        PostListFragment.PostListFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //PostListFragment postListFragment = (PostListFragment) getSupportFragmentManager().findFragmentById(R.id.postListView);
        PostListFragment postListFragment = (PostListFragment) getSupportFragmentManager().findFragmentByTag("postlist_fragment");

        DrawerData data = GlobalClass.instance().categoryList.get(position);
        if(data.name != "") {
            String url = String.format(GlobalClass.CATEGORY_RSSURL, data.name);
            String url1 = String.format(GlobalClass.CATEGORY_RSSURL1, data.name);
            postListFragment.rssURL = String.format(GlobalClass.CATEGORY_RSSURL, data.name);
            postListFragment.rssURL1 = String.format(GlobalClass.CATEGORY_RSSURL1, data.name1);
            Toast.makeText(this, url+ " " +data.name + "  " + url1 + " "+ data.name1 , Toast.LENGTH_SHORT).show();


        } else {
            postListFragment.rssURL = GlobalClass.RSSURL;
            postListFragment.rssURL1 = GlobalClass.RSSURL1;
            Toast.makeText(this, GlobalClass.instance().RSSURL, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, GlobalClass.instance().RSSURL1, Toast.LENGTH_SHORT).show();
        }


        PostDataModel.getInstance().listData.clear();
        PostDataModel.getInstance().guidList.clear();
        postListFragment.manualRefresh();
    }

    @Override
    public void onPostSelected(int index) {
        PostData data = PostDataModel.getInstance().listData.get(index);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.postLink));
        startActivity(browserIntent);
        /*if(postViewFragment == null) {
            postViewFragment = PostViewFragment.newInstance(data.postLink);
        } else {
            postViewFragment.urlLink = data.postLink;
        }

        postViewFragment.title = data.postTitle;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, postViewFragment, "postview_fragment");
        ft.addToBackStack(null);
        ft.commit();
        */
    }
}
