/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.gridviewpager;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // res와 pager 변수를 상수로 지정한다.
        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        // 페이저에 윈도우 틀을 적용하는 이벤트를 지정한다.
        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {

            // 윈도우 틀을 지정할 때 실행되는 메소드이다.
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {

                // 시계가 원형인지 판단한다.
                final boolean round = insets.isRound();

                // 페이지 행의 여백
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);

                // 페이지 열의 여백
                // 원형 시계라면 더 많은 여백을 준다.
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round :
                        R.dimen.page_column_margin);

                // 페이저의 여백을 지정한다.
                pager.setPageMargins(rowMargin, colMargin);

                // 파라미터로 넘어온 변수를 그대로 반환한다.
                return insets;
            }
        });

        // 페이저의 속성과 페이지 리스트를 담고 있는 어답터를 추가한다.
        pager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager()));
    }
}
