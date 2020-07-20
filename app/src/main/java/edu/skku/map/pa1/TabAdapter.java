package edu.skku.map.pa1;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public TabAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PersonalFragment personalFragment = new PersonalFragment();
                return  personalFragment;
            case 1:
                PublicFragment publicFragment = new PublicFragment();
                return publicFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}
