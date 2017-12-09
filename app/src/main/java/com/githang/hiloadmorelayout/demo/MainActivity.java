package com.githang.hiloadmorelayout.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author Geek_Soledad (msdx.android@qq.com)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ultra_load_more:
                startActivity(UltraLoadMoreActivity.class);
                break;
            case R.id.recycler_view_load_more:
                startActivity(RecyclerViewActivity.class);
                break;
            default:
        }
    }

    private void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }
}
