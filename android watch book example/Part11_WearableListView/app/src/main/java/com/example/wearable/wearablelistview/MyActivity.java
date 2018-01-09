package com.example.wearable.wearablelistview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity implements WearableListView.ClickListener {

    // 리스트 아이템
    public static WearableListItem[] wearableListItems;

    // 액티비티 생성 시 실행되는 메소드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // 웨어러블 리스트 뷰 생성
        WearableListView listView = (WearableListView) findViewById(R.id.list);
        listView.setAdapter(new Adapter(this));
        listView.setClickListener(this);

        // 아이템 생성
        wearableListItems = new WearableListItem[]{
                new WearableListItem("Item1", "Item1 Click!"),
                new WearableListItem("Item2", "Item2 Click!"),
                new WearableListItem("Item3", "Item3 Click!")
        };
    }

    // 리스트 아이템 클릭 시 실행되는 메소드
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        // 태그로부터 클릭한 인덱스
        Integer index = (Integer) viewHolder.itemView.getTag();

        // 인덱스에 해당하는 텍스트 출력
        String outputText = wearableListItems[index].mMessage;
        Toast.makeText(this, outputText, Toast.LENGTH_SHORT).show();
    }

    // 아이템이 아닌 비어있는 영역 클릭 시 실행되는 메소드
    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "Empty Region Clicked!", Toast.LENGTH_SHORT).show();
    }

    // 리스트 아이템 클래스
    private class WearableListItem {

        public String mName; // 아이템 이름
        public String mMessage; // 아이템을 클릭했을 때 나타날 메시지

        // 아이템 생성자
        public WearableListItem(String name, String message) {
            this.mName = name;
            this.mMessage = message;
        }
    }

    // 웨어러블 리스트 뷰 어답터
    private static final class Adapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private Adapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        // 리스트 뷰 성능 향상을 위한 뷰 홀더
        @Override
        public WearableListView.ViewHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {
            View wearableListItemLayout = mInflater.inflate(R.layout.wearable_list_item, null);

            return new WearableListView.ViewHolder(wearableListItemLayout);
        }

        // 뷰 홀더에 아이템을 적용시키는 메소드
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            // 텍스트를 입력한다.
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            view.setText(wearableListItems[position].mName);

            // 포지션을 태그에 넣는다.
            holder.itemView.setTag(position);
        }

        // 리스트 아이템 개수
        @Override
        public int getItemCount() {
            return wearableListItems.length;
        }
    }
}
