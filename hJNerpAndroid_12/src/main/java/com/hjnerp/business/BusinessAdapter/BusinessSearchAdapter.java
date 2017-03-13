package com.hjnerp.business.BusinessAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjnerp.model.EjMyWProj1345;
import com.hjnerpandroid.R;

import java.util.List;

/**
 * Created by Admin on 2017/1/17.
 */

public class BusinessSearchAdapter extends RecyclerView.Adapter<BusinessSearchAdapter.MyViewHolder> {
    private Context mContext;
    private List<EjMyWProj1345> list;

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public BusinessSearchAdapter(Context mContext, List<EjMyWProj1345> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.bus_search_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.item_peoject.setText(list.get(position).getName_wproj());
        holder.item_client.setText(list.get(position).getName_corr());
        holder.item_line_id.setText(String.valueOf((position + 1)));
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(list.get(position).getName_wproj(), list.get(position).getName_corr(),
                            list.get(position).getId_wproj(),list.get(position).getId_corr(),position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_peoject;
        TextView item_client;
        TextView item_line_id;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_peoject = (TextView) itemView.findViewById(R.id.item_peoject);
            item_client = (TextView) itemView.findViewById(R.id.item_client);
            item_line_id = (TextView) itemView.findViewById(R.id.item_line_id);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(String item_peoject, String item_client, String id_wproj,String id_corr, int position);

        void onItemLongClick(View view, int position);
    }

}
