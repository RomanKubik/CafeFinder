package com.example.kubik.cafefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.helpers.ImageConverter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindArray;

/**
 * Activity to configure NavigationDrawer for all activities
 * except LoginActivity and CreateAccountActivity.
 * Created by Kubik on 12/21/16.
 */

public class BaseDrawerCafeActivity extends BaseCafeActivity {

    private static AccountHeader sAccountHeader;
    private static ProfileDrawerItem sProfileItem;
    private static PrimaryDrawerItem itemHome;
    private static PrimaryDrawerItem itemFavourite;
    private static PrimaryDrawerItem itemSearch;
    private static SecondaryDrawerItem itemSettings;
    private static SecondaryDrawerItem itemLogOut;

    private static int sFavouriteCafeCount = 0;

    protected Drawer mDrawer;
    protected Toolbar mToolbar;

    static {
        sProfileItem = new ProfileDrawerItem()
                .withName(sProfile.getName())
                .withEmail(sProfile.getEmail())
                .withIcon(ImageConverter.byteToBitmapConverter(sProfile.getPhoto()));

        itemHome = new PrimaryDrawerItem()
                .withIdentifier(0)
                .withName(R.string.nav_draw_home);

        itemFavourite = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.nav_draw_favourite);

        itemSearch = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.nav_draw_search);

        itemSettings = new SecondaryDrawerItem()
                .withIdentifier(3)
                .withName(R.string.nav_draw_settings);

        itemLogOut = new SecondaryDrawerItem()
                .withIdentifier(4)
                .withName(R.string.nav_draw_logout);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sFavouriteCafeCount = sProfile.getFavouriteList().size();
        itemFavourite.withBadge(String.valueOf(sFavouriteCafeCount));
    }

    protected void initToolbar(int toolbarId) {
        mToolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(mToolbar);
        initDrawer();
    }

    private void initDrawer() {
        sAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.logo_cafe_profile_background)
                .withTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .addProfiles(sProfileItem)
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withSliderBackgroundColorRes(R.color.colorBackground)
                .withToolbar(mToolbar)
                .withAccountHeader(sAccountHeader)
                .addDrawerItems(
                        itemHome,
                        itemFavourite,
                        itemSearch,
                        new DividerDrawerItem(),
                        itemSettings,
                        itemLogOut
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        ShowSelectedActivity(position);
                        Log.d("MyTag", String.valueOf(position));
                        return true;
                    }
                })
                .build();
    }

    private void ShowSelectedActivity(int position) {
        switch (position) {
            case 1:
                showNearbyList();
                break;
            case 2:
                showFavouriteList();
                break;
            case 3:
                showSearchActivity();
                break;
            case 5:
                showSettingsActivity();
                break;
            case 6:
                logout();
                break;
            default:
                break;
        }
    }


    private void showNearbyList() {
        if (this instanceof MainActivity) {
            MainActivity activity = (MainActivity) this;
            activity.setNearbyListMode();
            mDrawer.closeDrawer();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showFavouriteList() {
        if (this instanceof MainActivity) {
            MainActivity activity = (MainActivity) this;
            activity.setFavouriteListMode();
            mDrawer.closeDrawer();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showSearchActivity() {

    }

    private void showSettingsActivity() {

    }

    private void logout() {

    }

    protected void addCafeToFavourite() {
        sFavouriteCafeCount++;
        itemFavourite.withBadge(String.valueOf(sFavouriteCafeCount));
        mDrawer.updateItem(itemFavourite);
    }

    protected void removeCafeFromFavourite() {
        sFavouriteCafeCount--;
        itemFavourite.withBadge(String.valueOf(sFavouriteCafeCount));
        mDrawer.updateItem(itemFavourite);
    }

}
