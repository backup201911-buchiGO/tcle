package org.blogsite.youngsoft.piggybank.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import org.blogsite.youngsoft.piggybank.R;

public class Splash extends Activity {

    AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.background_splash);
        ImageView loading = (ImageView) findViewById(R.id.tcle_loading);
        if (loading == null) {
            Log.v("YKKIM", "tcle_loading ImageView 객체가 널임.");
        } else {
            Log.v("YKKIM", loading.getDrawable() + "!!!!!");
        }
        animation = (AnimationDrawable) loading.getDrawable();
        if (animation != null) {
            animation.start();
        } else {
            Log.v("YKKIM", "animation 객체가 널임.");
        }
        //animation.start();




        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초

    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), SnsLoginActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동

            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

}
