package com.lakshitasuman.musicstation.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.fragment.FragmentAlbum;
import com.lakshitasuman.musicstation.musicplayer.fragment.FragmentArtist;
import com.lakshitasuman.musicstation.musicplayer.fragment.FragmentFolder;
import com.lakshitasuman.musicstation.musicplayer.fragment.FragmentPlaylist;
import com.lakshitasuman.musicstation.musicplayer.fragment.FragmentSongs;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;

public class HomeActivity extends AppCompatActivity {
    public static ImageView btnNext;
    public static ImageView btnPlayPause;
    public static ImageView btnPrevious;
    public static LinearLayout llControl;
    public static TextView txt1;
    public static TextView txt2;
    public final String TAG = getClass().getSimpleName();
    private FrameLayout adContainerView;
    ImageView btnAlbum;
    ImageView btnArtist;
    ImageView btnFolders;
    ImageView btnPlaylist;
    ImageView btnSongs;
    ImageView img1;
    FragmentAlbum fragmentAlbum;
    FragmentArtist fragmentArtist;
    FragmentFolder fragmentFolder;
    FragmentPlaylist fragmentPlaylist;
    FragmentSongs fragmentSongs;
    ImageView search;
    ImageView timer;
    CardView img_1;
    TextView titleText;
    ViewPager viewpager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_home);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsview));
        init();
        clicks();
        addTabsToViewpager(viewpager);
        viewpager.setCurrentItem(2);
        ((ImageView) findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        img1.setImageResource(R.drawable.song_images1);

    }

    private void clicks() {
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsUtils.initAd(HomeActivity.this);
                AdsUtils.showInterAd(HomeActivity.this,new Intent(HomeActivity.this, TimerActivity.class));
              //  startActivity(new Intent(HomeActivity.this, TimerActivity.class));
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(0);
                btnAlbum.setImageResource(R.drawable.albums_presed);
                btnArtist.setImageResource(R.drawable.artists_unpresed);
                btnSongs.setImageResource(R.drawable.songs_unpresed);
                btnFolders.setImageResource(R.drawable.folders_unpresed);
                btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
            }
        });
        btnArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(1);
                btnAlbum.setImageResource(R.drawable.albums_unpresed);
                btnArtist.setImageResource(R.drawable.artists_presed);
                btnSongs.setImageResource(R.drawable.songs_unpresed);
                btnFolders.setImageResource(R.drawable.folders_unpresed);
                btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
            }
        });
        btnSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(2);
                btnAlbum.setImageResource(R.drawable.albums_unpresed);
                btnArtist.setImageResource(R.drawable.artists_unpresed);
                btnSongs.setImageResource(R.drawable.songs_presed);
                btnFolders.setImageResource(R.drawable.folders_unpresed);
                btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
            }
        });
        btnFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(3);
                btnAlbum.setImageResource(R.drawable.albums_unpresed);
                btnArtist.setImageResource(R.drawable.artists_unpresed);
                btnSongs.setImageResource(R.drawable.songs_unpresed);
                btnFolders.setImageResource(R.drawable.folders_presed);
                btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
            }
        });
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(4);
                btnAlbum.setImageResource(R.drawable.albums_unpresed);
                btnArtist.setImageResource(R.drawable.artists_unpresed);
                btnSongs.setImageResource(R.drawable.songs_unpresed);
                btnFolders.setImageResource(R.drawable.folders_unpresed);
                btnPlaylist.setImageResource(R.drawable.playlist_presed);
            }
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PlayerService.class);
                intent.setAction(Constants.ACTION.PLAY_ACTION);
                startService(intent);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerActivity.previous.performClick();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerActivity.next.performClick();
            }
        });
        llControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (PlayerActivity.mMediaPlayer.isPlaying()) {
                        Intent intent = new Intent(HomeActivity.this, PlayerActivity.class);
                        intent.putExtra("fromControl", "true");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slideup, R.anim.noanimation);
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "onClick: " + exception);
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });
    }

    private void init() {
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        btnAlbum = (ImageView) findViewById(R.id.btnAlbum);
        btnArtist = (ImageView) findViewById(R.id.btnArtist);
        btnSongs = (ImageView) findViewById(R.id.btnSongs);
        btnFolders = (ImageView) findViewById(R.id.btnFolers);
        img1 = (ImageView) findViewById(R.id.img1);
        img_1 = (CardView) findViewById(R.id.img_1);
        btnPlaylist = (ImageView) findViewById(R.id.btnPlaylist);
        timer = (ImageView) findViewById(R.id.timer);
        btnPlayPause = (ImageView) findViewById(R.id.btnPlayPause);
        btnPrevious = (ImageView) findViewById(R.id.btnPrevious);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        llControl = (LinearLayout) findViewById(R.id.llControl);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        search = (ImageView) findViewById(R.id.search);
        titleText = (TextView) findViewById(R.id.titleText);
    }



    public void addTabsToViewpager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentAlbum = new FragmentAlbum();
        fragmentArtist = new FragmentArtist();
        fragmentSongs = new FragmentSongs();
        fragmentFolder = new FragmentFolder();
        fragmentPlaylist = new FragmentPlaylist();
        viewPagerAdapter.addFragment(fragmentAlbum);
        viewPagerAdapter.addFragment(fragmentArtist);
        viewPagerAdapter.addFragment(fragmentSongs);
        viewPagerAdapter.addFragment(fragmentFolder);
        viewPagerAdapter.addFragment(fragmentPlaylist);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        titleText.setText("Albums");
                        viewpager.setCurrentItem(0);
                        btnAlbum.setImageResource(R.drawable.albums_presed);
                        btnArtist.setImageResource(R.drawable.artists_unpresed);
                        btnSongs.setImageResource(R.drawable.songs_unpresed);
                        btnFolders.setImageResource(R.drawable.folders_unpresed);
                        btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
                        return;
                    case 1:
                        titleText.setText("Artist");
                        viewpager.setCurrentItem(1);
                        btnAlbum.setImageResource(R.drawable.albums_unpresed);
                        btnArtist.setImageResource(R.drawable.artists_presed);
                        btnSongs.setImageResource(R.drawable.songs_unpresed);
                        btnFolders.setImageResource(R.drawable.folders_unpresed);
                        btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
                        return;
                    case 2:
                        titleText.setText("Songs");
                        viewpager.setCurrentItem(2);
                        btnAlbum.setImageResource(R.drawable.albums_unpresed);
                        btnArtist.setImageResource(R.drawable.artists_unpresed);
                        btnSongs.setImageResource(R.drawable.songs_presed);
                        btnFolders.setImageResource(R.drawable.folders_unpresed);
                        btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
                        return;
                    case 3:
                        titleText.setText("Folders");
                        viewpager.setCurrentItem(3);
                        btnAlbum.setImageResource(R.drawable.albums_unpresed);
                        btnArtist.setImageResource(R.drawable.artists_unpresed);
                        btnSongs.setImageResource(R.drawable.songs_unpresed);
                        btnFolders.setImageResource(R.drawable.folders_presed);
                        btnPlaylist.setImageResource(R.drawable.playlist_unpresed);
                        return;
                    case 4:
                        titleText.setText("Playlists");
                        viewpager.setCurrentItem(4);
                        btnAlbum.setImageResource(R.drawable.albums_unpresed);
                        btnArtist.setImageResource(R.drawable.artists_unpresed);
                        btnSongs.setImageResource(R.drawable.songs_unpresed);
                        btnFolders.setImageResource(R.drawable.folders_unpresed);
                        btnPlaylist.setImageResource(R.drawable.playlist_presed);
                        return;
                    default:
                        return;
                }
            }
        });
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter  {
        List<Fragment> fragmentList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }



        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }


    @Override
    public void onResume() {
        try {
            if (PlayerActivity.mMediaPlayer != null) {
                if (PlayerActivity.mMediaPlayer.isPlaying()) {
                    llControl.setVisibility(View.VISIBLE);
                    btnPlayPause.setImageResource(R.drawable.pause);
                } else {
                    llControl.setVisibility(View.GONE);
                    btnPlayPause.setImageResource(R.drawable.play);
                }
            }
        } catch (Exception exception) {
            String str = TAG;
            Log.e(str, "onResume: " + exception);
        }
        super.onResume();
    }

}
