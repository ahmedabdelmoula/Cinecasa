/*
* Copyright (C) 2015 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package esprit.tn.cinecasa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.ppamorim.dragger.DraggerPanel;

import butterknife.InjectView;
import esprit.tn.cinecasa.utils.Context;

public class PanelActivity extends AbstractActivity {

    private LayoutInflater layoutInflater;

    @InjectView(R.id.dragger_panel)
    DraggerPanel draggerPanel;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_panel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        configLayoutInflater();
        getSupportActionBar().hide();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        draggerPanel.initializeView();
        draggerPanel.addViewOnDrag(
                layoutInflater.inflate(R.layout.layout_content, draggerPanel, false));
        draggerPanel.addViewOnShadow(
                layoutInflater.inflate(R.layout.layout_shadow, draggerPanel, false));

        ImageButton movies = (ImageButton) findViewById(R.id.movies);
        ImageButton tv_series = (ImageButton) findViewById(R.id.tv_series);

        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draggerPanel.closeActivity();
                Context.SELECTED_TYPE = true;
            }
        });

        tv_series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draggerPanel.closeActivity();
                Context.SELECTED_TYPE = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        draggerPanel.closeActivity();
    }

    private void configLayoutInflater() {
        layoutInflater = LayoutInflater.from(this);
    }

}