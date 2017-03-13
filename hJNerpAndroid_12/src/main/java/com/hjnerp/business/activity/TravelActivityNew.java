package com.hjnerp.business.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hjnerp.activity.MainActivity;
import com.hjnerp.adapter.BusinessBillsAdapter;
import com.hjnerp.business.BusinessJsonCallBack.BJsonCallBack;
import com.hjnerp.business.businessutils.DateUtil;
import com.hjnerp.common.ActivitySupport;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.model.BusinessBillsMessages;
import com.hjnerp.model.Ctlm1345;
import com.hjnerp.model.Ej1345;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.util.StringUtil;
import com.hjnerp.util.ToastUtil;
import com.hjnerp.widget.WaitDialogRectangle;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class TravelActivityNew extends ActivitySupport implements View.OnClickListener {
    private WaitDialogRectangle waitDialog;
    private PullToRefreshListView refresh_travel_act;
    private Button add_travel_act;
    private RelativeLayout activity_travel_new;
    private List<PerformanceDatas> datas = new ArrayList<>();
    private final int HTTP_SUCCESS = 0;//数据请求成功
    private final int HTTP_LOSER = 1;//数据请求成功
    private BusinessBillsAdapter billsAdapter;
    private int index;
    private List<Ctlm1345> users;
    private String userID;
    private String id_menu = Constant.ID_MENU;
    public static TravelActivityNew travelActivityNew = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setViewData();
                    break;
                case 1:
                    String content = (String) msg.obj;
                    ToastUtil.ShowLong(getApplicationContext(), content);
                    waitDialog.dismiss();
                    refresh_travel_act.onRefreshComplete();
                    break;
                case 2:
                    datas.remove(index);
                    refreshList();
                    break;
            }
        }
    };

    public void refresh() {
        datas.remove(index);
//        refresh_travel_act.onRefreshComplete();
        refreshList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_new);
        travelActivityNew = this;
        initView();
    }

    /**
     * 刷新listview中的数据
     */
    private void refreshList() {
        billsAdapter.refreshList(datas);
        // listItemAdapter.notifyDataSetChanged();
        refresh_travel_act.onRefreshComplete();
    }

    private void initView() {
        refresh_travel_act = (PullToRefreshListView) findViewById(R.id.refresh_travel_act);
        add_travel_act = (Button) findViewById(R.id.add_travel_act);
        activity_travel_new = (RelativeLayout) findViewById(R.id.activity_travel_new);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch (id_menu) {
            case "002035":
                getSupportActionBar().setTitle("出差/外出单");
                break;
            case "002040":
                getSupportActionBar().setTitle("休假申请单");
                break;
            case "002090":
                getSupportActionBar().setTitle("加班申请单");
                break;
            case "002095":
                getSupportActionBar().setTitle("考勤异常申诉");
                break;
            default:
                getSupportActionBar().setTitle("和佳ERP");
                break;

        }

        users = new ArrayList<>();
        users = BusinessBaseDao.getCTLM1345ByIdTable("user");
        if (users.size() == 0) {
            ToastUtil.ShowShort(this, "请先下载基础数据");
            finish();
            return;
        }
        String userinfos = users.get(0).getVar_value();
//        id_clerk =
        Gson gson1 = new Gson();
        Ej1345 ej1345 = gson1.fromJson(userinfos, Ej1345.class);
        userID = ej1345.getId_user();
        add_travel_act.setOnClickListener(this);
        waitDialog = new WaitDialogRectangle(this);
        waitDialog.show();
        refresh_travel_act.setMode(PullToRefreshBase.Mode.PULL_FROM_START);// 仅下拉刷新
        addListViewData();

        // //上下拉刷新
        refresh_travel_act.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getBusinessList(id_menu, userID);
            }


            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
            }

        });

    }

    private void setViewData() {
        Collections.sort(datas, new Comparator<PerformanceDatas>() {
            @Override
            public int compare(PerformanceDatas lhs, PerformanceDatas rhs) {
                Date date1 = DateUtil.stringToDate(lhs.getMain().getDate_opr());
                Date date2 = DateUtil.stringToDate(rhs.getMain().getDate_opr());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;

            }
        });
        for (PerformanceDatas pd : datas) {
            if (pd.getMain() != null) {
                billsAdapter = new BusinessBillsAdapter(this, datas);
                refresh_travel_act.setAdapter(billsAdapter);
                billsAdapter.notifyDataSetChanged();
                refresh_travel_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Constant.performanceDatas = getPerformanceDatas(position - 1);
                        Constant.JUDGE_TYPE = false;
                        Log.d("datas", Constant.performanceDatas.toString());
                        Intent intent = new Intent();

                        switch (id_menu) {
                            case "002035":
                                intent.setClass(getApplicationContext(), TravelBusiness.class);
                                break;
                            case "002040":
                                intent.setClass(getApplicationContext(), LeaveBusiness.class);

                                break;
                            case "002090":
                                intent.setClass(getApplicationContext(), OverBusiness.class);

                                break;
                            case "002095":
                                intent.setClass(getApplicationContext(), AbnormalBusiness.class);

                                break;
                            default:
                                break;

                        }
                        startActivityForResult(intent, 11);
                    }
                });
                waitDialog.dismiss();
                refresh_travel_act.onRefreshComplete();
            } else {
                sendMessage(HTTP_LOSER, "数据为空");
            }
        }
    }

    // 获得WorkInfo
    private PerformanceDatas getPerformanceDatas(int position) {
        PerformanceDatas info = null;
        index = position;
        info = datas.get(position);
        return info;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // //界面返回值
        /**
         * @author haijian
         * 收到返回的值判断是否成功，如果同意就将数据移除刷新列表
         */
        if (requestCode == 11 && resultCode == 22) {
            handler.sendEmptyMessage(2);
            MainActivity.WORK_COUNT = MainActivity.WORK_COUNT - 1;
        } else if (requestCode == 11 && resultCode == 33) {
            handler.sendEmptyMessage(2);
        }
    }

    /**
     * 从网络加载数据
     */
    private void addListViewData() {
        // 如果有网络加载网络数据，并在加载成功后删除之前保留的本地数据，没有网络加载本地数据
        if (this.hasInternetConnected()) {
            if (StringUtil.isStrTrue(id_menu)) {
                getBusinessList(id_menu, userID);
            }
        } else {
            sendMessage(HTTP_LOSER, "请检查网络");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_travel_act:
                switch (id_menu) {
                    case "002035":
                        intentActivity(TravelBusiness.class);
                        break;
                    case "002040":
                        intentActivity(LeaveBusiness.class);


                        break;
                    case "002090":
                        intentActivity(OverBusiness.class);


                        break;
                    case "002095":
                        intentActivity(AbnormalBusiness.class);


                        break;
                    default:
                        break;

                }
                Constant.JUDGE_TYPE = true;
                break;
        }
    }

    private void intentActivity(Class c) {
        Intent itent = new Intent(this, c);
        startActivity(itent);
    }

    /**
     * 获取数据
     *
     * @param idmenu
     * @param iduser
     */
    private void getBusinessList(String idmenu, String iduser) {
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataQueryServlet")
                .params("idmenu", idmenu)
                .params("iduser", iduser)
                .execute(new BJsonCallBack<BusinessBillsMessages>() {
                    @Override
                    public void onSuccess(BusinessBillsMessages businessBillsMessages, Call call, Response response) {

                        datas = businessBillsMessages.getDatas();
                        Log.d("datas", datas.toString());
                        handler.sendEmptyMessage(HTTP_SUCCESS);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        sendMessage(HTTP_LOSER, e.getMessage());
                    }

                });
    }

    private void sendMessage(int tag, Object content) {
        Message message = new Message();
        message.what = tag;
        message.obj = content;
        handler.sendMessage(message);
    }

}
