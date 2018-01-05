package esprit.tn.cinecasa.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hanks.htextview.scale.ScaleTextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.R;

/**
 * Created by Yessine on 11/21/2017.
 */

public class HomeeFragment extends Fragment {

    private VpAdapter adapter;
    private List<Fragment> fragments;
    private View view;
    private LayoutInflater inflater;
    private ViewGroup container;
    public ScaleTextView type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homee, container, false);
        this.inflater = inflater;
        this.container = container;

        type = (ScaleTextView) view.findViewById(R.id.typeeee);

        ViewPager vpChild = (ViewPager) view.findViewById(R.id.vpChildd);
        vpChild.setOffscreenPageLimit(4);

        fragments = new ArrayList<>(4);

        initT();

        adapter = new VpAdapter(getChildFragmentManager(), fragments);
        vpChild.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(vpChild);

        vpChild.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0: type.animateText("Most Popular");
                        break;
                    case 1: type.animateText("Top Rated");
                        break;
                    case 2: type.animateText("Airing Today");
                        break;
                    case 3: type.animateText("On The Air");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type.animateText("Most Popular");
    }

    private void initT() {

        fragments.clear();

        Fragment frag = new TVShowsFragment();
        Bundle b = new Bundle();
        b.putString("urlmore", "https://api.themoviedb.org/3/tv/airing_today?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&page=");
        b.putString("url", "https://api.themoviedb.org/3/tv/airing_today?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US");
        frag.setArguments(b);

        Fragment frag1 = new TVShowsFragment();
        Bundle b1 = new Bundle();
        b1.putString("urlmore", "https://api.themoviedb.org/3/tv/on_the_air?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&page=");
        b1.putString("url", "https://api.themoviedb.org/3/tv/on_the_air?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US");
        frag1.setArguments(b1);

        Fragment frag2 = new TVShowsFragment();
        Bundle b2 = new Bundle();
        b2.putString("urlmore", "https://api.themoviedb.org/3/tv/popular?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&page=");
        b2.putString("url", "https://api.themoviedb.org/3/tv/popular?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US");
        frag2.setArguments(b2);

        Fragment frag3 = new TVShowsFragment();
        Bundle b3 = new Bundle();
        b3.putString("urlmore", "https://api.themoviedb.org/3/tv/top_rated?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&page=");
        b3.putString("url", "https://api.themoviedb.org/3/tv/top_rated?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US");
        frag3.setArguments(b3);


        fragments.add(frag);
        fragments.add(frag1);
        fragments.add(frag2);
        fragments.add(frag3);
    }

    public static class VpAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }
}
