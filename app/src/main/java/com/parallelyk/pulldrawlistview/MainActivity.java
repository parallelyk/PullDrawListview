package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private PullDragListview pullDragListview;
    private ListView listView;
    private List<HashMap<String,Object>> mListItem = new ArrayList<HashMap<String,Object>>();
    private MyAdapter adapter ;
    //ArrayAdapter<String> adapter;
    //String[] items = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L","A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullDragListview = (PullDragListview) findViewById(R.id.pdlistvew);
        initTestData();
        //listView = (ListView) findViewById(R.id.listview);
        adapter = new MyAdapter(this);
        pullDragListview.setAdapter(adapter);
    }
    private void initTestData(){
        for (int i = 0 ; i<20 ;i++){
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("title","标题"+i);
            hashMap.put("content","内容"+i);
            hashMap.put("time","时间"+i);
            hashMap.put("view",null);
            mListItem.add(hashMap);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "点击" , Toast.LENGTH_SHORT).show();
        Log.d("hhhhhh","hhhhhh");
    }


    public class MyAdapter extends BaseAdapter{
        private Context mContext;
        private LayoutInflater layoutInflater;

        public MyAdapter(Context context){
            mContext = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            // all items are separator
            return true;
        }

        @Override
        public boolean isEnabled(int position)
        {
            // all items are separator
            return true;
        }
        @Override
        public int getCount() {
            return mListItem.size();
        }

        @Override
        public Object getItem(int position) {//返回当前选中的view
            return mListItem.get(position).get("view");
        }

        public Object getData(int position){
            return mListItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            DragListItem dragListItem = (DragListItem) convertView;

            if(dragListItem == null){
                View view = layoutInflater.inflate(R.layout.list_item_drag,parent,false);
                dragListItem = new DragListItem(mContext);
                dragListItem.setContentView(view);
                viewHolder = new ViewHolder(dragListItem);
                dragListItem.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) dragListItem.getTag();
            }
            dragListItem.rollBack();

            final HashMap<String,Object> hashMap = (HashMap<String, Object>) getData(position);

            viewHolder.tv_title.setText(hashMap.get("title").toString());
            viewHolder.tv_content.setText(hashMap.get("content").toString());
            viewHolder.tv_time.setText(hashMap.get("time").toString());
            //HashMap<String,Object> tmpHashMap = new HashMap<>();
            hashMap.put("view", dragListItem);

            viewHolder.showItem.setOnTouchListener(new View.OnTouchListener() {//使用OnItemClick或OnClickListener的话会造成冲突
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean consume = false;
                    float x = 0, y = 0;
                    Log.d("aa","in");

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x = event.getX();
                            y = event.getY();
                            consume = true;
                            Log.d("aa","indown");
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Log.d("aa","ACTION_MOVE");
                            if (Math.abs(event.getX() - x) > ViewConfiguration.get(mContext).getScaledTouchSlop()|| Math.abs(event.getY() - y) > ViewConfiguration.get(mContext).getScaledTouchSlop()) {
                                Log.d("aa","flase");
                                consume = false;
                            } else {
                                Log.d("aa","true");
                                consume = true;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d("aa","ACTION_UP");
                            if (consume) {
                                Toast.makeText(mContext, "hahahhahah", Toast.LENGTH_SHORT).show();
                            }
                            break;

                    }

                    return false;
                }
            });
            viewHolder.hideItem.setOnClickListener(new View.OnClickListener() {//给显示的Item设置点击事件
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "删除" + hashMap.get("title"), Toast.LENGTH_SHORT).show();
                }
            });




            return dragListItem;
        }
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        ViewGroup hideItem;
        LinearLayout showItem;

        ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.list_item_title);
            tv_content = (TextView) view.findViewById(R.id.list_item_context);
            tv_time = (TextView) view.findViewById(R.id.list_item_time);
            hideItem = (ViewGroup) view.findViewById(R.id.hide_view);
            showItem = (LinearLayout) view.findViewById(R.id.show_content_view);
        }
    }

    /**
     * 放置每一次选中的itemView和数据
     */
    class DataHolder{
        DragListItem dragListItem;

    }
}
