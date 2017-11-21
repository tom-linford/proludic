package icn.proludic.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import icn.proludic.fragments.CommunityFragment;
import icn.proludic.fragments.FitnessFragment;
import icn.proludic.fragments.HomeFragment;
import icn.proludic.fragments.OffersFragment;
import icn.proludic.fragments.ProfileFragment;

/**
 * Author:  Bradley Wilson
 * Date: 11/04/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> pFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return HomeFragment.newInstance(position);
            case 1:
                return FitnessFragment.newInstance(position);
            case 2:
                return ProfileFragment.newInstance(position);
            case 3:
                return CommunityFragment.newInstance(position);
            case 4:
                return OffersFragment.newInstance(position);
            default:
                return HomeFragment.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return pFragmentList.size();
    }

    public void addFrag(android.support.v4.app.Fragment fragment) {
        pFragmentList.add(fragment);
    }
}
