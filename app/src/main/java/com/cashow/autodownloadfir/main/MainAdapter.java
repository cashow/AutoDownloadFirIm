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

    public MainAdapter(Context context) {
        this.context = context;
    }

    public void setFirInfoList(FirInfoList firInfoList) {
        this.firInfoList = firInfoList;
        notifyDataSetChanged();
    }

    public void addFirInfo(FirInfo firInfo) {
        firInfoList.infoList.add(firInfo);
        firInfoList.focusedPosition = firInfoList.infoList.size() - 1;
        notifyDataSetChanged();
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_fir_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.edittextName.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        viewHolder.edittextPassword.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {
        int holderPosition = holder.getAdapterPosition();
        FirInfo firInfo = firInfoList.infoList.get(holderPosition);

        setEdittextName(holder, firInfo);
        setEdittextPassword(holder, firInfo);

        holder.itemView.setOnClickListener(v -> holder.edittextName.requestFocus());
        holder.imageDelete.setOnClickListener(v -> {
            firInfoList.infoList.remove(firInfo);
            notifyDataSetChanged();
        });
        if (firInfoList.focusedPosition == holderPosition) {
            holder.itemView.setSelected(true);
            holder.edittextName.post(() -> holder.edittextName.requestFocus());
        }
    }

    private void setEdittextName(MainAdapter.ViewHolder holder, FirInfo firInfo) {
        if (holder.edittextName.getTag() != null) {
            TextWatcher oriTextWatcher = (TextWatcher) holder.edittextName.getTag();
            holder.edittextName.removeTextChangedListener(oriTextWatcher);
        }
        holder.edittextName.setText(firInfo.name);
        holder.edittextName.setOnFocusChangeListener((v, hasFocus) -> {
            holder.itemView.setSelected(hasFocus);
            if (hasFocus) {
                firInfoList.focusedPosition = holder.getAdapterPosition();
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
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
        };
        holder.edittextName.addTextChangedListener(textWatcher);
        holder.edittextName.setTag(textWatcher);
    }

    private void setEdittextPassword(MainAdapter.ViewHolder holder, FirInfo firInfo) {
        if (holder.edittextPassword.getTag() != null) {
            TextWatcher oriTextWatcher = (TextWatcher) holder.edittextPassword.getTag();
            holder.edittextPassword.removeTextChangedListener(oriTextWatcher);
        }
        holder.edittextPassword.setText(firInfo.password);
        holder.edittextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            holder.itemView.setSelected(hasFocus);
            if (hasFocus) {
                firInfoList.focusedPosition = holder.getAdapterPosition();
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
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
        };
        holder.edittextPassword.addTextChangedListener(textWatcher);
        holder.edittextPassword.setTag(textWatcher);
    }

    public FirInfoList getFirInfoList() {
        return firInfoList;
    }

    public FirInfo getCurrentFirInfo() {
        if (firInfoList.focusedPosition < 0 || firInfoList.focusedPosition >= firInfoList.infoList.size()) {
            return new FirInfo("", "");
        }
        return firInfoList.infoList.get(firInfoList.focusedPosition);
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
