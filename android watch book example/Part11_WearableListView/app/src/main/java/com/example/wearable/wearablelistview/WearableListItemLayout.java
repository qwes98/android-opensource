package com.example.wearable.wearablelistview;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WearableListItemLayout extends LinearLayout implements WearableListView.Item {

    private final float mFadedTextAlpha; // 선택되지 않은 텍스트 투명도
    private final int mFadedCircleColor; // 선택되지 않은 이미지 색상
    private final int mChosenCircleColor; // 선택된 이미지 색상

    private float mScale; // 에니메이션 중 현재 크기 비율

    private ImageView mCircle; // 왼쪽 아이콘 이미지 뷰
    private TextView mName; // 텍스트 뷰

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 생성자
    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mFadedTextAlpha = 0.4f;  // 선택되지 않은 아이템의 텍스트 투명도
        mFadedCircleColor = getResources().getColor(R.color.wl_gray);   // 선택되지 않은 아이템 색상
        mChosenCircleColor = getResources().getColor(R.color.wl_blue);  // 선택된 아이템의 색상
    }

    // 아이템의 xml이 객체화 된 후 실행되는 메소드
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCircle = (ImageView) findViewById(R.id.circle);
        mName = (TextView) findViewById(R.id.name);
    }

    // 최저 스케일 값
    @Override
    public float getProximityMinValue() {
        return 1f;
    }

    // 최대 스케일 값
    @Override
    public float getProximityMaxValue() {
        return 1.6f;
    }

    // 현재 스케일 값 반환
    @Override
    public float getCurrentProximityValue() {
        return mScale;
    }

    // 확대, 축소 에니메이션 진행 스케일 지정
    @Override
    public void setScalingAnimatorValue(float scale) {
        mScale = scale;
        mCircle.setScaleX(scale);
        mCircle.setScaleY(scale);
    }

    // 확대 에니메이션을 시작하기 전 실행 메소드
    @Override
    public void onScaleUpStart() {
        mName.setAlpha(1f);
        ((GradientDrawable) mCircle.getDrawable()).setColor(mChosenCircleColor);
    }

    // 축소 에니메이션을 진행하기 전 실행 메소드
    @Override
    public void onScaleDownStart() {
        mName.setAlpha(mFadedTextAlpha);
        ((GradientDrawable) mCircle.getDrawable()).setColor(mFadedCircleColor);
    }
}