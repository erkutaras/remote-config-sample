package com.erkutaras.remoteconfigsample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int cacheExpiration = 3600;
    private TextView mTextMessage;
    private TextView mTextStatus;
    private FrameLayout mContainer;
    private View mHomeView;
    private View mDefaultView;
    private RadioGroup mRadioGroup;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initContent();
        initFirebaseProducts();
    }

    private void initContent() {
        mContainer = findViewById(R.id.frameLayout);
        mTextMessage = findViewById(R.id.title);
        mTextStatus = findViewById(R.id.status);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mHomeView = getLayoutInflater().inflate(R.layout.view_home, mContainer, false);
        mContainer.addView(mHomeView);
        mDefaultView = getLayoutInflater().inflate(R.layout.view_values, mContainer, false);
        mRadioGroup = mDefaultView.findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRadioGroup.clearCheck();
                mFirebaseAnalytics.setUserProperty(
                        "favorite_color",
                        String.valueOf(group.findViewById(checkedId).getTag()));
            }
        });
    }

    private void initFirebaseProducts() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        fetchAndActivate(null);
    }

    public void fetchAndActivate(View view) {
        mTextStatus.setText(R.string.message_fetch);
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mTextStatus.setText(R.string.message_success);
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            mTextStatus.setText(R.string.message_fail);
                        }
                    }



                });
    }

    public void onClickGitHub(View view) {
        openBrowser("https://github.com/erkutaras/remote-config-sample");
    }

    public void onClickMedium(View view) {
        openBrowser("https://medium.com/@erkutaras_45701");
    }

    public void onClickWebPage(View view) {
        openBrowser("http://www.erkutaras.com/");
    }

    private void openBrowser(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mContainer.removeAllViews();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    mContainer.addView(mHomeView);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_default);
                    mContainer.addView(mDefaultView);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_fetched);
                    return true;
            }
            return false;
        }
    };

    private Map<String, Integer> mColorMap = new HashMap<String, Integer>() {{
        put("black", Color.BLACK);
        put("blue", Color.BLUE);
        put("cyan", Color.CYAN);
        put("yellow", Color.YELLOW);
        put("gray", Color.GRAY);
        put("green", Color.GREEN);
        put("magenta", Color.MAGENTA);
        put("red", Color.RED);
        put("white", Color.WHITE);
    }};
}
