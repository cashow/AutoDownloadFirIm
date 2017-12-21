package com.cashow.autodownloadfir.main;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cashow.autodownloadfir.R;
import com.cashow.autodownloadfir.main.model.FirInfo;
import com.cashow.autodownloadfir.main.model.FirInfoList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private FirInfoList firInfoList = new FirInfoList();

    private Context context;

    private int currentFocusedPosition;

    public MainAdapter(Context context) {
        this.context = context;
    }

    public void setFirInfoList(FirInfoList firInfoList) {
        this.firInfoList = firInfoList;
        notifyDataSetChanged();
    }

    public void addFirInfo(FirInfo firInfo) {
        firInfoList.infoList.add(firInfo);
        notifyDataSetChanged();
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_fir_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {
        FirInfo firInfo = firInfoList.infoList.get(position);
        holder.edittextName.setText(firInfo.name);
        holder.edittextName.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.edittextName.setOnFocusChangeListener((v, hasFocus) -> {
            holder.itemView.setSelected(hasFocus);
            if (hasFocus) {
                currentFocusedPosition = position;
            }
        });
        holder.edittextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                firInfo.name = s.toString();
            }
        });

        holder.edittextPassword.setText(firInfo.password);
        holder.edittextPassword.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.edittextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            holder.itemView.setSelected(hasFocus);
            if (hasFocus) {
                currentFocusedPosition = position;
            }
        });
        holder.edittextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                firInfo.password = s.toString();
            }
        });

        holder.itemView.setOnClickListener(v -> holder.edittextName.requestFocus());
        holder.imageDelete.setOnClickListener(v -> {
            firInfoList.infoList.remove(firInfo);
            notifyDataSetChanged();
        });
        if (firInfo.isFocused) {
            holder.itemView.setSelected(true);
            holder.edittextName.post(() -> holder.edittextName.requestFocus());
        }
    }

    public FirInfoList getFirInfoList() {
        return firInfoList;
    }

    public FirInfo getCurrentFirInfo() {
        if (currentFocusedPosition < 0 || currentFocusedPosition >= firInfoList.infoList.size()) {
            return new FirInfo("", false, "");
        }
        return firInfoList.infoList.get(currentFocusedPosition);
    }

    @Override
    public int getItemCount() {
        return firInfoList.infoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.edittext_name)
        EditText edittextName;
        @BindView(R.id.edittext_password)
        EditText edittextPassword;
        @BindView(R.id.image_delete)
        View imageDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
