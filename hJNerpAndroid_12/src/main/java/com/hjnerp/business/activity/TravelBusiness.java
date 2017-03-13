package com.hjnerp.business.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjnerp.adapter.TravelAdapter;
import com.hjnerp.business.BusinessJsonCallBack.BFlagCallBack;
import com.hjnerp.common.ActivitySupport;
import com.hjnerp.common.Constant;
import com.hjnerp.common.EapApplication;
import com.hjnerp.dao.BusinessBaseDao;
import com.hjnerp.dao.QiXinBaseDao;
import com.hjnerp.model.BusinessFee;
import com.hjnerp.model.Ctlm1345;
import com.hjnerp.model.Ctlm7502Json;
import com.hjnerp.model.Ej1345;
import com.hjnerp.model.PerformanceBean;
import com.hjnerp.model.PerformanceDatas;
import com.hjnerp.model.UserInfo;
import com.hjnerp.model.businessFlag;
import com.hjnerp.net.HttpClientBuilder;
import com.hjnerp.net.HttpClientManager;
import com.hjnerp.util.ToastUtil;
import com.hjnerp.util.myscom.StringUtils;
import com.hjnerpandroid.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.OkGoException;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Response;

public class TravelBusiness extends ActivitySupport implements View.OnClickListener {

    private EditText travel_name;
    private EditText travel_part;
    private EditText travel_time_begin;
    private EditText travel_time_end;
    private EditText travel_reason;
    private AutoCompleteTextView travel_work;
    private EditText travel_client;
    private LinearLayout addlayout;
    private TextView addtravel;
    private Button submit_travel;
    private int i = 1;
    private List<View> listaddview;
    private EditText travel_partner;
    private Calendar c = Calendar.getInstance();
    private UserInfo myInfo;
    private String username;
    private String userID;
    private String departmentName;
    private String companyID;
    private String id_dept;
    private EditText travel_pay;
    private TextView nofocus;
    private HttpClientManager.HttpResponseHandler responseHandler = new NsyncDataHandler();
    public static final String JSON_VALUE = "values";
    public static final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
    private List<Ctlm7502Json> travelDatas;
    private List<Ctlm1345> fees;
    private String[] feeItems;
    private String[] feeItemsMark;
    private String[] feeId;
    private ArrayAdapter<String> stringArrayAdapter;
    private String id_proj;
    private String id_corr;
    private double dec_samt;
    private List<Ctlm1345> users;
    private String id_clerk;
    private String id_linem;
    private String id_auditlvl;
    private EditText var_rejust_name;
    private LinearLayout rejust_list;
    private PerformanceDatas pds;
    private PerformanceDatas.MainBean mainBean;
    private List<PerformanceBean> details;
    private EditText traveltimefrom;
    private EditText travelfrom;
    private EditText travelto;
    private EditText traveltimeto;
    private Spinner travelpayfee;
    private EditText travelothers;
    private EditText traveldetailpayfee;
    private int countDetail;
    private int mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_business);

        initData();
        initView();
    }

    private void initData() {


        travelDatas = new ArrayList<>();
        fees = new ArrayList<>();
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
        id_clerk = ej1345.getId_clerk();
        id_linem = ej1345.getId_linem();
        id_auditlvl = ej1345.getId_auditlvl();
        fees = BusinessBaseDao.getCTLM1345ByIdTable("fee");

        feeItems = new String[fees.size() + 1];
        feeItems[0] = "";
        feeItemsMark = new String[fees.size() + 1];
        feeItemsMark[0] = "";
        feeId = new String[fees.size() + 1];
        feeId[0] = "";
        for (int i = 0; i < fees.size(); i++) {
            String feevalue = fees.get(i).getVar_value();
            Gson gson = new Gson();
            BusinessFee businessFee = gson.fromJson(feevalue, BusinessFee.class);
            feeItems[i + 1] = businessFee.getName_fee();
            feeItemsMark[i + 1] = businessFee.getVar_remark();
            feeId[i + 1] = businessFee.getId_fee();
        }
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, feeItems);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        try {
            HttpClientBuilder.HttpClientParam param = HttpClientBuilder
                    .createParam(Constant.NBUSINESS_SERVICE_ADDRESS);
            param.addKeyValue(Constant.BM_ACTION_TYPE, "MobileSyncDataDownload")
                    .addKeyValue("id_table", StringUtils.join("ctlm7502_corr"))
                    .addKeyValue("condition", "1=1");

            HttpClientManager.addTask(responseHandler, param.getHttpPost());

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void initView() {

        getSupportActionBar().show();
        getSupportActionBar().setTitle("出差/外出单");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        travel_name = (EditText) findViewById(R.id.travel_name);
        travel_part = (EditText) findViewById(R.id.travel_part);
        travel_time_begin = (EditText) findViewById(R.id.travel_time_begin);
        travel_time_begin.setOnClickListener(this);
        travel_time_end = (EditText) findViewById(R.id.travel_time_end);
        travel_time_end.setOnClickListener(this);
        travel_reason = (EditText) findViewById(R.id.travel_reason);
        travel_work = (AutoCompleteTextView) findViewById(R.id.travel_work);
        travel_client = (EditText) findViewById(R.id.travel_client);
        addlayout = (LinearLayout) findViewById(R.id.addlayout);
        addtravel = (TextView) findViewById(R.id.addtravel);
        addtravel.setOnClickListener(this);
        submit_travel = (Button) findViewById(R.id.submit_travel);
        submit_travel.setOnClickListener(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.travel_detail, addlayout, false);
        TextView traveldelete1 = (TextView) view1.findViewById(R.id.travel_delete);
        traveldelete1.setVisibility(View.GONE);
        addlayout.addView(view1);
        traveltimefrom = (EditText) view1.findViewById(R.id.travel_time_from);
        travelfrom = (EditText) view1.findViewById(R.id.travel_from);
        travelto = (EditText) view1.findViewById(R.id.travel_to);
        traveltimeto = (EditText) view1.findViewById(R.id.travel_time_to);
        travelpayfee = (Spinner) view1.findViewById(R.id.travel_pay_work);
        travelothers = (EditText) view1.findViewById(R.id.travel_others);
        traveldetailpayfee = (EditText) view1.findViewById(R.id.travel_pay_fee);

        travelpayfee.setAdapter(stringArrayAdapter);
        traveltimefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showtime(traveltimefrom);
            }
        });
        traveltimeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showtime(traveltimeto);
            }
        });
        travelpayfee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mark != i) {
                    travelothers.setText(feeItemsMark[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        listaddview = new ArrayList<View>();
        listaddview.add(view1);
        travel_partner = (EditText) findViewById(R.id.travel_partner);
        travel_pay = (EditText) findViewById(R.id.travel_pay);
        travel_pay.setOnClickListener(this);
        nofocus = (TextView) findViewById(R.id.nofocus);
        nofocus.requestFocus();
        myInfo = QiXinBaseDao.queryCurrentUserInfo();
        username = myInfo.username;
        userID = myInfo.userID;
        companyID = myInfo.companyID;
        id_dept = myInfo.departmentID;
//        clerkID = myInfo.clerkID;
//        Toast.makeText(this,clerkID,Toast.LENGTH_SHORT).show();
//        departmentID = myInfo.departmentID;
        travel_name.setText(username);
        departmentName = myInfo.departmentName;
        travel_part.setText(departmentName);
        TravelAdapter adapter = new TravelAdapter(travelDatas, this);
        travel_work.setAdapter(adapter);
        travel_work.setThreshold(1);
        travel_work.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ctlm7502Json pc = (Ctlm7502Json) adapterView.getAdapter().getItem(i);
                id_proj = pc.getId_proj().trim();
                id_corr = pc.getId_corr().trim();
                travel_work.setText(pc.getName_proj().trim());
                travel_client.setText(pc.getName_corr().trim());
            }
        });
        var_rejust_name = (EditText) findViewById(R.id.var_rejust_name);
        rejust_list = (LinearLayout) findViewById(R.id.rejust_list);
        if (!Constant.JUDGE_TYPE) {
            rejust_list.setVisibility(View.VISIBLE);
            setRejustContext();
        }
    }


    private void setRejustContext() {
        pds = Constant.performanceDatas;
        mainBean = pds.getMain();
        details = pds.getDetails();
        travel_partner.setText(mainBean.getVar_clerk());
        travel_time_begin.setText(mainBean.getDate_begin());
        travel_time_end.setText(mainBean.getDate_end());
        travel_reason.setText(mainBean.getRemark());
        travel_work.setText(mainBean.getName_proj().trim());
        travel_client.setText(mainBean.getName_corr().trim());
        id_proj = mainBean.getId_proj();
        id_corr = mainBean.getId_corr();
        travel_pay.setText(mainBean.getDec_pamt());
        var_rejust_name.setText(details.get(0).getVar_rejust());
        travelfrom.setText(details.get(0).getVar_start());
        travelto.setText(details.get(0).getVar_end());
        String var_begin_end_time = details.get(0).getVar_time();
        if (var_begin_end_time.contains("/")) {
            String[] var_time = var_begin_end_time.split("/");
            if (var_begin_end_time.startsWith("/")) {
                traveltimeto.setText(var_time[0]);

            } else if (var_begin_end_time.endsWith("/")) {
                traveltimefrom.setText(var_time[0]);
            } else {
                traveltimefrom.setText(var_time[0]);
                traveltimeto.setText(var_time[1]);
            }

        }
        for (int i = 0; i < feeId.length; i++) {
            if (feeId[i].equalsIgnoreCase(details.get(0).getId_fee())) {
                travelpayfee.setSelection(i, true);
                mark = i;
            }
        }
        traveldetailpayfee.setText(details.get(0).getDec_amt());
        travelothers.setText(details.get(0).getVar_remark());
        countDetail = details.size();
        this.i = countDetail;
        for (int i = 0; i < details.size(); i++) {
            if (i != 0) {
                int mark_sine = 0;
                LayoutInflater inflater = LayoutInflater.from(this);
                final View view = inflater.inflate(R.layout.travel_detail, addlayout, false);
                addlayout.addView(view);
                listaddview.add(view);
                TextView nun = (TextView) view.findViewById(R.id.travel_num);
                nun.setText("行程明细" + (i + 1));
                TextView traveldelete = (TextView) view.findViewById(R.id.travel_delete);
                traveldelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelview(view);
                    }
                });
                final EditText traveltimefrom2 = (EditText) view.findViewById(R.id.travel_time_from);
                final EditText travelfrom2 = (EditText) view.findViewById(R.id.travel_from);
                final EditText travelto2 = (EditText) view.findViewById(R.id.travel_to);
                final EditText traveltimeto2 = (EditText) view.findViewById(R.id.travel_time_to);
                final EditText travelothers2 = (EditText) view.findViewById(R.id.travel_others);
                final Spinner travelpayfee2 = (Spinner) view.findViewById(R.id.travel_pay_work);
                final EditText traveldetailpayfee2 = (EditText) view.findViewById(R.id.travel_pay_fee);
                traveldetailpayfee2.setText(details.get(i).getDec_amt());
                travelothers2.setText(details.get(i).getVar_remark());
                travelpayfee2.setAdapter(stringArrayAdapter);
                traveltimefrom2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showtime(traveltimefrom2);
                    }
                });
                traveltimeto2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showtime(traveltimeto2);
                    }
                });
                for (int j = 0; j < feeId.length; j++) {
                    if (feeId[j].equalsIgnoreCase(details.get(i).getId_fee())) {
                        travelpayfee2.setSelection(j, true);
                        mark_sine = j;
                    }
                }
                final int finalMark_sine = mark_sine;
                travelpayfee2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i != finalMark_sine) {
                            travelothers2.setText(feeItemsMark[i]);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                travelfrom2.setText(details.get(i).getVar_start());
                travelto2.setText(details.get(i).getVar_end());
                String var_begin_end_time2 = details.get(i).getVar_time();
                if (var_begin_end_time2.contains("/")) {
                    String[] var_time = var_begin_end_time2.split("/");
                    if (var_begin_end_time2.startsWith("/")) {
                        traveltimeto2.setText(var_time[1]);
                    } else if (var_begin_end_time2.endsWith("/")) {
                        traveltimefrom2.setText(var_time[0]);
                    } else {
                        traveltimefrom2.setText(var_time[0]);
                        traveltimeto2.setText(var_time[1]);
                    }

                }


            }


        }
        TravelAdapter adapter = new TravelAdapter(travelDatas, this);
        travel_work.setAdapter(adapter);
        travel_work.setThreshold(1);
        travel_work.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ctlm7502Json pc = (Ctlm7502Json) adapterView.getAdapter().getItem(i);
                id_proj = pc.getId_proj().trim();
                id_corr = pc.getId_corr().trim();
                travel_work.setText(pc.getName_proj().trim());
                travel_client.setText(pc.getName_corr().trim());
            }
        });


    }


    private class NsyncDataHandler extends HttpClientManager.HttpResponseHandler {

        @Override
        public void onException(Exception e) {

        }

        @Override
        public void onResponse(HttpResponse resp) {
            // TODO Auto-generated method stub
            try {
                String contentType = resp.getHeaders("Content-Type")[0]
                        .getValue();
                // if ("application/octet-stream".equals(contentType) ) {
                if (contentType.indexOf("application/octet-stream") != -1) {
                    String contentDiscreption = resp
                            .getHeaders("Content-Disposition")[0].getValue();
                    String fileName = contentDiscreption
                            .substring(contentDiscreption.indexOf("=") + 1);
                    FileOutputStream fos = new FileOutputStream(new File(
                            getExternalCacheDir(), fileName));
                    resp.getEntity().writeTo(fos);
                    fos.close();
                    String json = processBusinessCompress(fileName);
                    JSONObject jsonObject = new JSONObject(json);
                    String value = jsonObject.getString(JSON_VALUE);

                    Log.d("value", value);
                    processJsonValue(value);
                } else {

                }

            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void processJsonValue(String value) throws JSONException {
        // TODO Auto-generated method stub

        if (value.equalsIgnoreCase("[]") || value.equalsIgnoreCase(null)) {
            return;
        }
        JSONArray jsonArray = new JSONArray(value);

        for (int i = 0; i < jsonArray.length(); i++) {
            String temp = jsonArray.getString(i);
            Matcher m = p.matcher(temp);
            String subValue = temp.substring(temp.indexOf("{"),
                    temp.indexOf("}") + 1);
            // 2.存入1347
            Gson gson = new Gson();
            Ctlm7502Json ctlm7502Json = gson.fromJson(subValue, Ctlm7502Json.class);
            travelDatas.add(ctlm7502Json);

        }

    }

    private String processBusinessCompress(String fileName) {
        // TODO Auto-generated method stub
        ZipInputStream zis = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            File f = new File(getExternalCacheDir(), fileName);
            FileInputStream fis = new FileInputStream(f);
            zis = new ZipInputStream(fis);
            ZipEntry zip = zis.getNextEntry();
            int len = 0;
            while ((len = zis.read(data)) != -1) {
                baos.write(data, 0, len);
            }
            String json = new String(baos.toByteArray(), HTTP.UTF_8);
            return json;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    private void submit() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String a = f.format(c.getTime());
        String name = travel_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "出差人员不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String partner = travel_partner.getText().toString().trim();
        String begin = travel_time_begin.getText().toString().trim();
        if (TextUtils.isEmpty(begin)) {
            Toast.makeText(this, "开始时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String end = travel_time_end.getText().toString().trim();
        if (TextUtils.isEmpty(end)) {
            Toast.makeText(this, "结束时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String reason = travel_reason.getText().toString().trim();
        if (TextUtils.isEmpty(reason)) {
            Toast.makeText(this, "出差原因不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String pay = travel_pay.getText().toString().trim();
        HjUpload hjUpload = new HjUpload(this);
        try {
            if (hjUpload.compare(end, begin)) {
                ToastUtil.ShowShort(this, "开始时间不能大于结束时间。");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // TODO validate success, do something
        for (int i = 0; i < listaddview.size(); i++) {
            View view3 = listaddview.get(i);
            EditText travelpayfee = (EditText) view3.findViewById(R.id.travel_pay_fee);
            if (!TextUtils.isEmpty(travelpayfee.getText().toString())) {
                try {
                    dec_samt = dec_samt + Double.parseDouble(travelpayfee.getText().toString().trim());
                } catch (Exception e) {
                    ToastUtil.ShowShort(this, "金额项中请输入正确的数字格式");
                }
            }
        }

        StringBuffer stringBuffer = new StringBuffer();

        if (!Constant.JUDGE_TYPE) {
            stringBuffer.append("{\"tableid\":\"dgtdout\",\"opr\":\"SS\",\"no\":\"" + mainBean.getDgtdout_no() + "\",\"userid\":\"" + userID + "\",\"comid\":\"" + companyID + "\",");
            stringBuffer.append("\"menuid\":\"002035\",\"dealtype\":\"save\",\"data\":[");
        } else {
            stringBuffer.append("[{key:{\"tableid\":\"dgtdout\",\"opr\":\"SS\",\"no\":\"\",\"userid\":\"" + userID + "\",\"comid\":\"" + companyID + "\",\"data\":[");
        }
        int gap = 0;
        int cycleTime = 0;
        if (Constant.JUDGE_TYPE) {
            gap = 0;
            cycleTime = listaddview.size();

        } else {
            gap = listaddview.size() - countDetail;
            if (gap >= 0) {
                cycleTime = listaddview.size();
            } else {
                cycleTime = countDetail;
            }
        }

        for (int i = 0; i < cycleTime; i++) {
            if (!Constant.JUDGE_TYPE) {
                if (i >= countDetail) {
                    stringBuffer.append("{\"table\": \"dgtdout_03\",\"oprdetail\":\"N\",\"where\":\" \",\"data\":[");
                } else if (i >= countDetail + gap) {
                    stringBuffer.append("{\"table\": \"dgtdout_03\",\"oprdetail\":\"D\",\"where\":\" \",\"data\":[");
                } else {
                    stringBuffer.append("{\"table\": \"dgtdout_03\",\"oprdetail\":\"U\",\"where\":\" \",\"data\":[");
                }
            } else {
                stringBuffer.append("{\"table\": \"dgtdout_03\",\"where\":\" \",\"data\":[");
            }

            stringBuffer.append("{\"column\":\"flag_sts\",\"value\":\"L\",\"datatype\":\"varchar\"},");
            if (!Constant.JUDGE_TYPE) {
                stringBuffer.append("{\"column\":\"id_recorder\",\"value\":\"" + userID + "\",\"datatype\":\"varchar\"},");
                stringBuffer.append("{\"column\":\"date_opr\",\"value\":\"" + a + "\",\"datatype\":\"datetime\"}, ");
                stringBuffer.append("{\"column\":\"flag_psts\",\"value\":\"\",\"datatype\":\"varchar\"},");
                stringBuffer.append("{\"column\":\"id_table\",\"value\":\"\",\"datatype\":\"varchar\"},");

            }
            stringBuffer.append("{\"column\":\"id_flow\",\"value\":\"FBgtd\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_dept\",\"value\":\"" + id_dept + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_proj\",\"value\":\"" + id_proj + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"id_corr\",\"value\":\"" + id_corr + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"var_clerk\",\"value\":\"" + partner + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"remark\",\"value\":\"" + reason + "\",\"datatype\":\"varchar\"},");
            stringBuffer.append("{\"column\":\"line_no\",\"value\":\"" + (i + 1) + "\",\"datatype\":\"int\"}, ");
            stringBuffer.append("{\"column\":\"dec_samt\",\"value\":\"" + dec_samt + "\",\"datatype\":\"int\"},");
            stringBuffer.append("{\"column\":\"date_begin\",\"value\":\"" + begin + "\",\"datatype\":\"datetime\"}, ");
            stringBuffer.append("{\"column\":\"date_end\",\"value\":\"" + end + "\",\"datatype\":\"datetime\"}, ");
            String id_fee = " ";
            String var_remark = " ";
            String dec_amt = " ";
            String travelfromt = " ";
            String travelfromn = " ";
            String travelton = " ";
            String traveltot = " ";
            if (i < listaddview.size()) {
                View view3 = listaddview.get(i);
                EditText travelfrom = (EditText) view3.findViewById(R.id.travel_from);
                EditText travelto = (EditText) view3.findViewById(R.id.travel_to);
                EditText traveltimefrom1 = (EditText) view3.findViewById(R.id.travel_time_from);
                EditText traveltimeto1 = (EditText) view3.findViewById(R.id.travel_time_to);
                Spinner travelpaywork = (Spinner) view3.findViewById(R.id.travel_pay_work);
                EditText travelpayfee = (EditText) view3.findViewById(R.id.travel_pay_fee);
                EditText travelothers = (EditText) view3.findViewById(R.id.travel_others);
                id_fee = feeId[travelpaywork.getSelectedItemPosition()];
                var_remark = travelothers.getText().toString().trim();
                dec_amt = travelpayfee.getText().toString().trim();
                travelfromn = travelfrom.getText().toString().trim();
                travelton = travelto.getText().toString().trim();
                travelfromt = traveltimefrom1.getText().toString().trim();
                traveltot = traveltimeto1.getText().toString().trim();
            }
            stringBuffer.append("{\"column\":\"id_fee\",\"value\":\"" + id_fee + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_remark\",\"value\":\"" + var_remark + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"dec_amt\",\"value\":\"" + dec_amt + "\",\"datatype\":\"int\"}, ");
            stringBuffer.append("{\"column\":\"var_start\",\"value\":\"" + travelfromn + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_end\",\"value\":\"" + travelton + "\",\"datatype\":\"varchar\"}, ");
            if (TextUtils.isEmpty(travelfromt) && TextUtils.isEmpty(traveltot)) {
                stringBuffer.append("{\"column\":\"var_time\",\"value\":\"" + "" + "\",\"datatype\":\"varchar\"}, ");
            } else {
                stringBuffer.append("{\"column\":\"var_time\",\"value\":\"" + travelfromt + "/" + traveltot + "\",\"datatype\":\"varchar\"}, ");
            }
            stringBuffer.append("{\"column\":\"dec_pamt\",\"value\":\"" + pay + "\",\"datatype\":\"int\"}, ");
            stringBuffer.append("{\"column\":\"id_user\",\"value\":\"" + userID + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"id_clerk\",\"value\":\"" + id_clerk + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"id_linem\",\"value\":\"" + id_linem + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"id_auditlvl\",\"value\":\"" + id_auditlvl + "\",\"datatype\":\"varchar\"}, ");
            stringBuffer.append("{\"column\":\"var_title\",\"value\":\"" + username + "\",\"datatype\":\"varchar\"}]}");
            if (i != cycleTime - 1) {
                stringBuffer.append(",");
            }
        }
        if (!Constant.JUDGE_TYPE) {
            stringBuffer.append("]}");
            String str = stringBuffer.toString();
            Log.d("str1", str);
            getBusinessList(str, "save");
            String save = "\"menuid\":\"002035\",\"dealtype\":\"save\",\"data\":[";
            String send = "\"menuid\":\"002035\",\"dealtype\":\"send\",\"data\":[";
            String finalstring = str.replace(save, send);
            Log.d("str2", finalstring);
            getBusinessList(finalstring, "send");
        } else {
            stringBuffer.append("]},value:\"\"}]");
            String str = stringBuffer.toString();
            try {
                JSONArray jsonArray = new JSONArray(str);
                hjUpload.onhjupload(jsonArray);
                resetAll();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交数据
     *
     * @param datas
     */
    private void getBusinessList(String datas, final String dealtype) {
//        OkGo.post("http://172.16.12.243:8085/hjmerp/servlet/DataUpdateServlet")
        OkGo.post(EapApplication.URL_SERVER_HOST_HTTP + "/servlet/DataUpdateServlet")
                .isMultipart(true)
                .params("datas", datas)
                .execute(new BFlagCallBack<businessFlag>() {
                    @Override
                    public void onSuccess(businessFlag businessFlag, Call call, Response response) {
                        String content = businessFlag.getMessage();
                        Constant.billsNo = businessFlag.getNo();
                        if (businessFlag.getFlag().equals("Y")) {
                            if (dealtype.equalsIgnoreCase("send")) {
                                ToastUtil.ShowShort(getContext(), "上传成功");
//                                resetAll();
                                setResult(22);
                                finish();
                            }

                        } else {
                            ToastUtil.ShowShort(getContext(), "上传失败");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof OkGoException) {
                            ToastUtil.ShowShort(getContext(), "网络错误");

                        } else {
                            ToastUtil.ShowShort(getContext(), e.getMessage());

                        }
                    }
                });
    }

    private void resetAll() {
        dec_samt = 0;
        travel_time_begin.setText("");
        travel_time_end.setText("");
        travel_reason.setText("");
        travel_work.setText("");
        travel_client.setText("");
        addlayout.removeAllViews();
        i = 1;
        listaddview.clear();
        travel_partner.setText("");
        c = Calendar.getInstance();

        travel_pay.setText("");
        travelDatas.clear();
        fees.clear();
        users.clear();
        initData();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_travel:
                submit();
                break;
            case R.id.addtravel:
                i++;
                LayoutInflater inflater = LayoutInflater.from(this);
                final View view2 = inflater.inflate(R.layout.travel_detail, addlayout, false);
                addlayout.addView(view2);
                listaddview.add(view2);
                TextView nun = (TextView) view2.findViewById(R.id.travel_num);
                nun.setText("行程明细" + i);
                TextView traveldelete = (TextView) view2.findViewById(R.id.travel_delete);
                traveldelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelview(view2);
                    }
                });
                final EditText traveltimefrom2 = (EditText) view2.findViewById(R.id.travel_time_from);
                final EditText traveltimeto2 = (EditText) view2.findViewById(R.id.travel_time_to);
                final EditText travelothers2 = (EditText) view2.findViewById(R.id.travel_others);
                final Spinner travelpayfee2 = (Spinner) view2.findViewById(R.id.travel_pay_work);
                travelpayfee2.setAdapter(stringArrayAdapter);
                traveltimefrom2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showtime(traveltimefrom2);
                    }
                });
                traveltimeto2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showtime(traveltimeto2);
                    }
                });
                travelpayfee2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        travelothers2.setText(feeItemsMark[i]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                break;
            case R.id.travel_time_begin:
                showCalendar(travel_time_begin);
                break;
            case R.id.travel_time_end:
                showCalendar(travel_time_end);
                break;

        }

    }

    private void cancelview(final View view2) {

        final Dialog noticeDialog = new Dialog(this, R.style.noticeDialogStyle);
        noticeDialog.setContentView(R.layout.dialog_notice_withcancel);
        RelativeLayout dialog_cancel_rl, dialog_confirm_rl;
        TextView notice = (TextView) noticeDialog
                .findViewById(R.id.dialog_notice_tv);
        notice.setText("是否要删除该行程明细？");
        dialog_cancel_rl = (RelativeLayout) noticeDialog
                .findViewById(R.id.dialog_cc_cancel_rl);
        TextView dialog_cancel_tv = (TextView) noticeDialog
                .findViewById(R.id.dialog_cancel_tv);
        dialog_cancel_tv.setText("取消");
        dialog_confirm_rl = (RelativeLayout) noticeDialog
                .findViewById(R.id.dialog_cc_confirm_rl);
        TextView dialog_confirm_tv = (TextView) noticeDialog
                .findViewById(R.id.dialog_confirm_tv);
        dialog_confirm_tv.setText("删除");
        dialog_cancel_rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                noticeDialog.dismiss();
            }
        });
        dialog_confirm_rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                noticeDialog.dismiss();
                addlayout.removeView(view2);
//                        i--;
                listaddview.remove(view2);
            }
        });
        noticeDialog.show();
    }

    private void showtime(final EditText timetext) {

//        c.setTimeInMillis(System.currentTimeMillis());
        final StringBuilder str = new StringBuilder("");
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear + 1;
                        if (month < 10 && dayOfMonth < 10) {
                            str.append(year + "-0" + month
                                    + "-0" + dayOfMonth);
                        } else if (month < 10 && dayOfMonth >= 10) {
                            str.append(year + "-0" + month
                                    + "-" + dayOfMonth);
                        } else if (month >= 10 && dayOfMonth < 10) {
                            str.append(year + "-" + month
                                    + "-0" + dayOfMonth);
                        } else {
                            str.append(year + "-" + month
                                    + "-" + dayOfMonth);
                        }
                        Calendar time = Calendar.getInstance();
                        new TimePickerDialog(TravelBusiness.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                if (minute < 10) {
                                    str.append(" " + hour + ":0" + minute);
                                    timetext.setText(str);
                                } else {
                                    str.append(" " + hour + ":" + minute);
                                    timetext.setText(str);
                                }
                            }
                        }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true).show();
                    }
                }
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                .get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showCalendar(final EditText editText) {
//        c = Calendar.getInstance();
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear + 1;
                        if (month < 10 && dayOfMonth < 10) {
                            editText.setText(year + "-0" + month
                                    + "-0" + dayOfMonth);
                        } else if (month < 10 && dayOfMonth >= 10) {
                            editText.setText(year + "-0" + month
                                    + "-" + dayOfMonth);
                        } else if (month >= 10 && dayOfMonth < 10) {
                            editText.setText(year + "-" + month
                                    + "-0" + dayOfMonth);
                        } else {
                            editText.setText(year + "-" + month
                                    + "-" + dayOfMonth);
                        }

                    }
                }
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                .get(Calendar.DAY_OF_MONTH)).show();
    }


}
