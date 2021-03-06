package com.shixipai.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.shixipai.BuildConfig;
import com.shixipai.R;
import com.shixipai.api.ApiClient;
import com.shixipai.bean.strategy.StrategyItem;
import com.shixipai.support.PrefUtils;
import com.shixipai.support.ResourceHelper;
import com.shixipai.ui.BaseActivity;
import com.shixipai.ui.feedback.FeedbackActivity;
import com.shixipai.ui.home.HomeFragment;
import com.shixipai.ui.jobFeedback.list.PostedJobListFragment;
import com.shixipai.ui.jobcollect.JobCollectActivity;
import com.shixipai.ui.resume.ResumeFragment;
import com.shixipai.ui.search.SearchActivity;
import com.shixipai.ui.update.UpdateDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by xiepeng on 16/1/13.
 */
public class MainActivity extends BaseActivity implements MainView,View.OnClickListener{

    private static final String[] DRAWER_TITLES = ResourceHelper.getStringArrays(R.array.drawer_list_items);

    @Inject
    MainPresenter mMainPresenter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.main_layout_home)
    LinearLayout layout_home;

    @Bind(R.id.tag_home)
    TextView tag_home;

    @Bind(R.id.ic_home)
    ImageView ic_home;

    @Bind(R.id.main_layout_job)
    LinearLayout layout_job;

    @Bind(R.id.tag_job)
    TextView tag_job;

    @Bind(R.id.ic_job)
    ImageView ic_job;

    @Bind(R.id.main_layout_resume)
    LinearLayout layout_resume;

    @Bind(R.id.tag_resume)
    TextView tag_resume;

    @Bind(R.id.ic_resume)
    ImageView ic_resume;

    private HomeFragment homeFragment;
    private PostedJobListFragment postedJobListFragment;
    private ResumeFragment resumeFragment;

    private AccountHeader mHeaderResult = null;
    private Drawer mResult = null;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;

        toolbar.setTitle("实习派");
        setSupportActionBar(toolbar);
        initialDrawer(savedInstanceState);

        replaceFragment(1);
        ic_home.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_home_selected));
        tag_home.setTextColor(ResourceHelper.getColor(R.color.color_primary));

        layout_home.setOnClickListener(this);
        layout_job.setOnClickListener(this);
        layout_resume.setOnClickListener(this);

        ApiClient.checkNewVersion(BuildConfig.VERSION_CODE + "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String result = response.getString("result");
                    if (result.equals("0")){
                        String url = response.getString("url");
                        String description = response.getString("describe");
                        UpdateDialogFragment.newInstance(url, description).show(MainActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initialDrawer(Bundle savedInstanceState){
        final IProfile profile = new ProfileDrawerItem()
                .withName(PrefUtils.getPrefUsername())
                .withIcon(ResourceHelper.getDrawable(R.drawable.ic_launcher))
                .withIdentifier(100);

        mHeaderResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.header)
                .withSelectionListEnabled(false)
                .addProfiles(profile)
                .withOnlyMainProfileImageVisible(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
//                        ProfileActivity.actionStart(mContext, PrefUtils.getPrefUid());
                        return false;
                    }
                })
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        mResult.closeDrawer();
//                        ProfileActivity.actionStart(mContext, PrefUtils.getPrefUid());
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        mResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withStatusBarColor(ResourceHelper.getColor(R.color.color_accent_dark))
                .withAccountHeader(mHeaderResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_setting)
                                .withIcon(R.mipmap.ic_drawer_setting)
                                .withIdentifier(1).withSelectable(true)
                                .withSelectedTextColor(ResourceHelper.getColor(R.color.color_primary))

                        , new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_share)
                                .withIcon(R.mipmap.ic_drawer_share)
                                .withIdentifier(2).withSelectable(true)
                                .withSelectedTextColor(ResourceHelper.getColor(R.color.color_primary))

                        , new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_feedback)
                                .withIcon(R.mipmap.ic_drawer_feedback)
                                .withIdentifier(3).withSelectable(true)
                                .withSelectedTextColor(ResourceHelper.getColor(R.color.color_primary))

                        , new PrimaryDrawerItem()
                                .withName("收藏")
                                .withIcon(R.mipmap.ic_drawer_collect)
                                .withIdentifier(4).withSelectable(true)
                                .withSelectedTextColor(ResourceHelper.getColor(R.color.color_primary))


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                   @Override
                                                   public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                                       if (drawerItem != null) {
                                                           mMainPresenter.selectDrawerItem(drawerItem.getIdentifier());
                                                       }
                                                       return false;
                                                   }
                                               }

                )
                .withSavedInstance(savedInstanceState)

                .withShowDrawerOnFirstLaunch(true)
                .build();
        if(savedInstanceState == null){
            mResult.setSelection(1,false);
            mHeaderResult.setActiveProfile(profile);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_search:
                SearchActivity.actionStart(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(mResult.isDrawerOpen()){
//            mResult.closeDrawer();
//            return true;
//        }
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
////            if (System.currentTimeMillis() - exitTime > 2000) {
////                Toast.makeText(this, getString(R.string.quit), Toast.LENGTH_SHORT).show();
////                exitTime = System.currentTimeMillis();
////            } else {
////                finish();
////                System.exit(0);
////            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new MainModule(this));
    }


    @Override
    public void replaceFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch (position) {
            case 1:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                fragment = homeFragment;
                break;
            case 2:
                if (postedJobListFragment == null) {
                    postedJobListFragment = new PostedJobListFragment();
                }
                fragment = postedJobListFragment;
                break;
            case 3:
                if (resumeFragment == null) {
                    resumeFragment = new ResumeFragment();
                }
                fragment = resumeFragment;
                break;
        }
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public void startSettingsActivity() {

    }

    @Override
    public void startFeedbackActivity() {
        FeedbackActivity.actionStart(this);
    }

    @Override
    public void startCollectActivity() {
        JobCollectActivity.actionStart(this);
    }

    @Override
    public void shareApp() {

    }

    @Override
    public void setUnSelect() {
        ic_home.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_home_normal));
        ic_job.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_job_normal));
        ic_resume.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_resume_normal));

        tag_home.setTextColor(ResourceHelper.getColor(R.color.color_light_grey));
        tag_job.setTextColor(ResourceHelper.getColor(R.color.color_light_grey));
        tag_resume.setTextColor(ResourceHelper.getColor(R.color.color_light_grey));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_layout_home:
                setUnSelect();
                replaceFragment(1);
                ic_home.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_home_selected));
                tag_home.setTextColor(ResourceHelper.getColor(R.color.color_primary));
                break;
            case R.id.main_layout_job:
                setUnSelect();
                replaceFragment(2);
                ic_job.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_job_selected));
                tag_job.setTextColor(ResourceHelper.getColor(R.color.color_primary));
                break;
            case R.id.main_layout_resume:
                setUnSelect();
                replaceFragment(3);
                ic_resume.setImageDrawable(ResourceHelper.getDrawable(R.mipmap.ic_resume_selected));
                tag_resume.setTextColor(ResourceHelper.getColor(R.color.color_primary));
                break;
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
