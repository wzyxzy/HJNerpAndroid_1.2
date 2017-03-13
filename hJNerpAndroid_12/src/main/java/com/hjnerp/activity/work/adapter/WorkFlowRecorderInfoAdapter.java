package com.hjnerp.activity.work.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjnerp.activity.work.ApprovalActivity;
import com.hjnerp.common.EapApplication;
import com.hjnerp.model.Cell;
import com.hjnerp.model.WorkflowApproveInfo;
import com.hjnerp.model.WorkflowDetailInfo;
import com.hjnerp.model.WorkflowListInfo;
import com.hjnerp.net.ChatConstants;
import com.hjnerp.net.ChatPacketHelper;
import com.hjnerp.util.ImageLoaderHelper;
import com.hjnerp.util.StringUtil;
import com.hjnerp.util.TableCell;
import com.hjnerp.widget.TableLayoutViews;
import com.hjnerpandroid.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WorkFlowRecorderInfoAdapter extends BaseAdapter {
    private static String TAG = "WorkFlowRecorderInfoAdapter";
    private Context context;
    private List<WorkflowApproveInfo> list;
    private WorkflowListInfo wfInfo;
    List<Map<String, List<WorkflowDetailInfo>>> tables;// 表格详情
    private Bitmap default_bitmap;

    public WorkFlowRecorderInfoAdapter(Context context,
                                       List<WorkflowApproveInfo> list, WorkflowListInfo wfInfo,
                                       List<Map<String, List<WorkflowDetailInfo>>> tables) {
        this.context = context;
        this.list = list;
        this.wfInfo = wfInfo;
        this.tables = tables;
        default_bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.v5_0_1_profile_headphoto);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void refreshList(List<WorkflowApproveInfo> list,
                            WorkflowListInfo wfInfo,
                            List<Map<String, List<WorkflowDetailInfo>>> tables) {
        // Log.e(TAG,"refreshList()");
        this.list = list;
        this.wfInfo = wfInfo;
        this.tables = tables;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.workdetail_item, parent, false);
            viewHolder.Approvalrlayout = (LinearLayout) view
                    .findViewById(R.id.rl_approval);
            viewHolder.picDetal = (ImageView) view
                    .findViewById(R.id.approval_photo_iv);
            viewHolder.picApproval = (ImageView) view
                    .findViewById(R.id.iv_workdetail_photo);
            viewHolder.resultApproval = (TextView) view
                    .findViewById(R.id.tv_workdetail_result);
            viewHolder.timeApproval = (TextView) view
                    .findViewById(R.id.tv_workdetail_time);
            viewHolder.nameApproval = (TextView) view
                    .findViewById(R.id.tv_workdetail_name);
            viewHolder.contentApproval = (TextView) view
                    .findViewById(R.id.tv_workdetail_content);
            viewHolder.picView = (ImageView) view.findViewById(R.id.tv_workdetail_ok);
            viewHolder.Detailllayout = (LinearLayout) view
                    .findViewById(R.id.ll_bill_detail);
            viewHolder.detail_ll_attach = (LinearLayout) view
                    .findViewById(R.id.ll_attach_approval);
            viewHolder.detail_ll_detail = (LinearLayout) view
                    .findViewById(R.id.ll_detail);
            viewHolder.detail_hs = (HorizontalScrollView) view
                    .findViewById(R.id.hs);

            viewHolder.detail_hs2 = (HorizontalScrollView) view
                    .findViewById(R.id.hs2);

            viewHolder.detail_hs3 = (HorizontalScrollView) view
                    .findViewById(R.id.hs3);

            viewHolder.nameDetail = (TextView) view
                    .findViewById(R.id.approval_name_tv);
            viewHolder.timeDetail = (TextView) view
                    .findViewById(R.id.approval_time_tv);
            viewHolder.titleDetail = (TextView) view
                    .findViewById(R.id.approval_title_tv);
            viewHolder.worrk_view1 = view
                    .findViewById(R.id.worrk_view1);
            viewHolder.worrk_view2 = view
                    .findViewById(R.id.worrk_view2);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (position == 0) {
            int tablecount = 0;
            viewHolder.Approvalrlayout.setVisibility(View.GONE);
            viewHolder.Detailllayout.setVisibility(View.VISIBLE);
            viewHolder.nameDetail.setText(wfInfo.getUser().getUserName());

            String time = wfInfo.getDate();// 服务器传过来的时间有毫秒，去掉毫秒
            if (time.split("\\.").length > 1) {
                time = time.split("\\.")[0];
            }
            viewHolder.timeDetail.setText(time);
            viewHolder.titleDetail.setText(wfInfo.getTitle());

            if (StringUtil.isNullOrEmpty(wfInfo.getUser().getAvatar())) {
                viewHolder.picApproval.setImageBitmap(default_bitmap);
            } else {
                String url = ChatPacketHelper.buildImageRequestURL(wfInfo.getUser().getAvatar(),
                        ChatConstants.iq.DATA_VALUE_RES_TYPE_ATTACH);

                ImageLoaderHelper.displayImage(url, viewHolder.picDetal);
            }

            // 加载附件名字和图标
            viewHolder.detail_ll_attach.removeAllViews();
            // Log.e(TAG, wfInfo.getAttach());
            if (!StringUtil.isNullOrEmpty(wfInfo.getAttach())) {
                String[] attachs = wfInfo.getAttach().split("\\|N");
                if (attachs.length > 0
                        && !StringUtil.isNullOrEmpty(attachs[0].trim())) {
                    for (int i = 0; i < attachs.length; i++) {
                        String[] pairAttach = attachs[i].split("\\|M");
                        final String attachName = pairAttach[1];
                        final String attachId = pairAttach[0];
                        View view1 = LayoutInflater.from(context)
                                .inflate(R.layout.textview_workflow_attach,
                                        null);
                        LayoutParams params = new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT);
                        viewHolder.detail_ll_attach.addView(view1, params);
                        TextView textView = (TextView) view1
                                .findViewById(R.id.tv_attach);
                        textView.setText(attachName);

                        LinearLayout ll_attach = (LinearLayout) view1
                                .findViewById(R.id.linear_attach);
                        ll_attach.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                ((ApprovalActivity) context).getAttach(wfInfo
                                        .getUser().getComID(), wfInfo
                                        .getBillType(), attachId, attachName);

                            }
                        });
                    }
                }
            }

            // 设置表格
            viewHolder.detail_ll_detail.removeAllViews();
            viewHolder.detail_hs.removeAllViews();
            viewHolder.detail_hs2.removeAllViews();
            viewHolder.detail_hs3.removeAllViews();
            if (tables != null) {
                for (int tableCounts = 0; tableCounts < tables.size(); tableCounts++) {
                    List<List<Cell>> mGrid = null;
                    for (String key : tables.get(tableCounts).keySet()) {
                        for (WorkflowDetailInfo t : tables.get(tableCounts)
                                .get(key)) {
                            if (t.form != null) {// 文本控件
                                for (Cell cell : t.form) {
                                    // Log.e(TAG, "form " + cell.title + ", " +
                                    // cell.value
                                    // + ", " + cell.type);
                                    View view1 = LayoutInflater.from(
                                            context).inflate(
                                            R.layout.textview_workflow_detail,
                                            null);
                                    LayoutParams params = new LayoutParams(
                                            LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT);
                                    viewHolder.detail_ll_detail.addView(view1,
                                            params);
                                    TextView textView = (TextView) view1
                                            .findViewById(R.id.tv_detail);
                                    textView.setTextSize(16);
                                    if (!"".equalsIgnoreCase(cell.title)) {
                                        textView.setText(cell.title + ": "  + cell.value);
                                    }

                                }
                            } else if (t.grid != null) { // TODO 表格控件
                                mGrid = new ArrayList<List<Cell>>();
                                mGrid = t.grid;
                            }
                        }
                    }


                    if (mGrid.size() > 0) {

                        if (tablecount == 0) {
                            viewHolder.detail_hs.addView(new TableLayoutViews(
                                    context, getTableItems(mGrid)));
                        }

                        if (tablecount == 1) {
                            viewHolder.detail_hs2.addView(new TableLayoutViews(
                                    context, getTableItems(mGrid)));
                        }

                        if (tablecount == 2) {
                            viewHolder.detail_hs3.addView(new TableLayoutViews(
                                    context, getTableItems(mGrid)));
                        }

                        tablecount++;
                    }
                }
            }

        } else {
            WorkflowApproveInfo info = list.get(position);
            // Log.e(TAG,"info >>>> 设置审批流程" + info.getTime()+
            // info.getUser().getUserName());
            viewHolder.Approvalrlayout.setVisibility(View.VISIBLE);
            viewHolder.Detailllayout.setVisibility(View.GONE);
            if (info.getOpinion().equals("Y")) {
                // viewHolder.resultApproval.setText("通过");
                viewHolder.resultApproval.setText("驳回");
                viewHolder.picView.setImageResource(R.drawable.workflow_reject);
            } else {
                // viewHolder.resultApproval.setText("驳回");
                viewHolder.resultApproval.setText("通过");
                viewHolder.picView.setImageResource(R.drawable.workflow_access);
            }
            if (position == 1) {
                viewHolder.worrk_view1.setVisibility(View.GONE);
            } else {
                viewHolder.worrk_view1.setVisibility(View.VISIBLE);
            }

            if (position == list.size() - 1) {
                viewHolder.worrk_view2.setVisibility(View.GONE);
            } else {
                viewHolder.worrk_view2.setVisibility(View.VISIBLE);
            }

            String time = info.getTime();// 服务器传过来的时间有毫秒，去掉毫秒
            if (time.split("\\.").length > 1) {
                time = time.split("\\.")[0];
            }
            viewHolder.timeApproval.setText(time);
            viewHolder.nameApproval.setText(info.getUser().getUserName());
            viewHolder.contentApproval.setText(info.getSuggest());

            if (StringUtil.isNullOrEmpty(info.getUser().getAvatar())) {
                viewHolder.picApproval.setImageBitmap(default_bitmap);
            } else {
                String url = ChatPacketHelper.buildImageRequestURL(info
                                .getUser().getAvatar(),
                        ChatConstants.iq.DATA_VALUE_RES_TYPE_ATTACH);

                ImageLoaderHelper.displayImage(url, viewHolder.picApproval);
            }

        }

        return view;
    }

    public static class ViewHolder {
        LinearLayout Detailllayout;// 单据详情布局
        TextView nameDetail; // 单据提交人名字
        TextView timeDetail; // 单据提交时间
        TextView titleDetail;// 单据标题
        ImageView picDetal;// 单据提交人头像
        LinearLayout detail_ll_attach, detail_ll_detail;
        HorizontalScrollView detail_hs;
        HorizontalScrollView detail_hs2;
        HorizontalScrollView detail_hs3;
        LinearLayout Approvalrlayout;// 审批布局
        ImageView picApproval; // 审批人头像
        TextView nameApproval; // 审批人人名称
        TextView resultApproval; // 审批结果
        TextView timeApproval; // 审批时间
        TextView contentApproval; // 附加审批意见
        ImageView picView;
        View worrk_view1;
        View worrk_view2;
    }

    private List<List<TableCell>> getTableItems(List<List<Cell>> mGrid) {
        List<List<TableCell>> list = new ArrayList<List<TableCell>>();
        for (List<Cell> cell : mGrid) {
            List<TableCell> rowItem = new ArrayList<TableCell>();
            Iterator<Cell> iter = cell.iterator();
            while (iter.hasNext()) {
                TableCell item = new TableCell();
                Cell subitem = iter.next();
                item.setName(subitem.title);
                item.setType(EapApplication.TEXT_TYPE);
                if (StringUtil.isNullOrEmpty(subitem.var_format.trim())) {
                    item.setValue((String) subitem.value);
                } else {
                    // item.setValue((String) subitem.value);
                    DecimalFormat dformat = new DecimalFormat(
                            subitem.var_format);
                    String data;
                    data = dformat.format(Double.parseDouble(subitem.value
                            .toString()));
                    item.setValue(data);
                }
                rowItem.add(item);
            }
            list.add(rowItem);
        }
        return list;

    }
}
