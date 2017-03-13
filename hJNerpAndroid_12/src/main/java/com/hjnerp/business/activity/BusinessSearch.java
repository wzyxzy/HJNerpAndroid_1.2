package com.hjnerp.business.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.hjnerp.business.BusinessAdapter.BusinessSearchAdapter;
import com.hjnerp.business.BusinessQueryDao.BusinessQueryDao;
import com.hjnerp.common.ActivitySupport;
import com.hjnerp.common.Constant;
import com.hjnerp.model.EjMyWProj1345;
import com.hjnerp.widget.ClearEditText;
import com.hjnerpandroid.R;

import java.util.List;

public class BusinessSearch extends ActivitySupport implements View.OnClickListener {
    private ClearEditText project_search;
    private RecyclerView project_recy;
    private CharSequence temp;//监听前的文本
    private BusinessSearchAdapter adapter;
    private Context mContext;
    private LinearLayout secah_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        setContentView(R.layout.activity_business_seach);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("项目搜索");
        mContext = this;
        initView();
    }

    private void initView() {
        project_search = (ClearEditText) findViewById(R.id.project_search);
        project_search.addTextChangedListener(textWatcher);
        project_recy = (RecyclerView) findViewById(R.id.project_recy);
        project_recy.setLayoutManager(new LinearLayoutManager(this));
        setHandlerMsg(0, project_search.getText().toString());
        secah_error = (LinearLayout) findViewById(R.id.secah_error);
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setHandlerMsg(0, s.toString());
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String content = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    getWProject(content);
                    break;
                case 1:

                    break;
            }
        }
    };

    private void setHandlerMsg(int numb, Object o) {
        Message message = new Message();
        message.what = numb;
        message.obj = o;
        mHandler.sendMessage(message);
    }

    public void getWProject(String content) {
        List<EjMyWProj1345> list = BusinessQueryDao.getMyProj(content, Constant.id_wtype, Constant.MYUSERINFO.userID);
        if (list.size() > 0) {
            project_recy.setVisibility(View.VISIBLE);
            secah_error.setVisibility(View.GONE);
            adapter = new BusinessSearchAdapter(this, list);
            adapter.notifyDataSetChanged();
            project_recy.setAdapter(adapter);
            adapter.setOnItemClickLitener(new BusinessSearchAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(String item_peoject, String item_client, String id_wproj, String id_corr, int position) {
                    Constant.item_peoject = item_peoject;
                    Constant.item_client = item_client;
                    Constant.id_wproj = id_wproj;
                    Constant.id_corr = id_corr;
                    setResult(22);
//                ToastUtil.ShowLong(mContext, item_peoject + "..." + item_client);
                    finish();
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        } else {
            project_recy.setVisibility(View.GONE);
            secah_error.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {

    }
}
