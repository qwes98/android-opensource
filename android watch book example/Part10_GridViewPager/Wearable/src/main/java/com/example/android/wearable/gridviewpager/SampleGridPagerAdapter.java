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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.ImageReference;
import android.view.Gravity;

public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;

    // 그리드 페이저 어답터의 생성자이다.
    public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    // 배경 이미지 배열이다.
    static final int[] BG_IMAGES = new int[]{
            R.drawable.debug_background_1,
            R.drawable.debug_background_2,
            R.drawable.debug_background_3,
            R.drawable.debug_background_4,
            R.drawable.debug_background_5
    };

    // 페이지의 속성 클래스
    private static class Page {
        int titleRes; // 타이틀 리소스
        int textRes; // 텍스트 리소스
        int iconRes; // 아이콘 리소스
        int cardGravity = Gravity.BOTTOM; // 카드 배치 위치
        boolean expansionEnabled = true; // 카드 확장 사용 여부
        float expansionFactor = 1.0f; // 카드 확장 될 비율
        int expansionDirection = CardFragment.EXPAND_DOWN; // 카드 확장 방향

        // 생성자
        public Page(int titleRes, int textRes, boolean expansion) {
            this(titleRes, textRes, 0);
            this.expansionEnabled = expansion;
        }

        public Page(int titleRes, int textRes, boolean expansion,
                    float expansionFactor) {
            this(titleRes, textRes, 0);
            this.expansionEnabled = expansion;
            this.expansionFactor = expansionFactor;
        }

        public Page(int titleRes, int textRes, int iconRes) {
            this.titleRes = titleRes;
            this.textRes = textRes;
            this.iconRes = iconRes;
        }

        public Page(int titleRes, int textRes, int iconRes, int gravity) {
            this.titleRes = titleRes;
            this.textRes = textRes;
            this.iconRes = iconRes;
            this.cardGravity = gravity;
        }
    }

    // 페이지 생성
    private final Page[][] PAGES = {
            {
                    new Page(R.string.welcome_title, R.string.welcome_text, R.drawable.bugdroid,
                            Gravity.CENTER_VERTICAL),
            },
            {
                    new Page(R.string.about_title, R.string.about_text, false),
            },
            {
                    new Page(R.string.cards_title, R.string.cards_text, true, 2),
                    new Page(R.string.expansion_title, R.string.expansion_text, true, 10),
            },
            {
                    new Page(R.string.backgrounds_title, R.string.backgrounds_text, true, 2),
                    new Page(R.string.columns_title, R.string.columns_text, true, 2)
            },
            {
                    new Page(R.string.dismiss_title, R.string.dismiss_text, R.drawable.bugdroid,
                            Gravity.CENTER_VERTICAL),
            },
    };

    @Override
    public Fragment getFragment(int row, int col) {
        // 열과 행에 해당하는 페이지를 가져와 page 객체로 생성한다.
        Page page = PAGES[row][col];

        // 카드 타이틀
        String title = page.titleRes != 0 ?
                mContext.getString(page.titleRes) : null;

        // 카드 텍스트
        String text = page.textRes != 0 ?
                mContext.getString(page.textRes) : null;

        // 카드 조각 객체 생성
        CardFragment fragment = CardFragment.create(title, text, page.iconRes);

        // 카드의 배치 위치
        fragment.setCardGravity(page.cardGravity);

        // 텍스트가 카드를 넘어섰을 때 컨텐트 확장 여부 설정
        fragment.setExpansionEnabled(page.expansionEnabled);

        // 카드 확장 기능을 적용했을 때 확장되는 방향
        fragment.setExpansionDirection(page.expansionDirection);

        // 카드 확장 기능을 적용했을 때 최대 확장되는 비율
        fragment.setExpansionFactor(page.expansionFactor);

        // 카드 조각 반환
        return fragment;
    }

    // 배경 이미지 반환한다.
    @Override
    public ImageReference getBackground(int row, int column) {
        return ImageReference.forDrawable(BG_IMAGES[row % BG_IMAGES.length]);
    }

    // 페이지의 행 개수를 반환한다.
    @Override
    public int getRowCount() {

        return PAGES.length;
    }

    // 페이지의 행 당 열 개수를 반환한다.
    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }
}
