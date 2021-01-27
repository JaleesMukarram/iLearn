package com.openlearning.ilearn.chat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.activities.SendChat;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.QueryUser;
import com.openlearning.ilearn.databinding.ViewSingleUserChatBinding;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllChatsAdapter extends RecyclerView.Adapter<AllChatsAdapter.UserChat> {

    private final Context context;
    private final List<QueryUser> queryUserList;

    public AllChatsAdapter(Context context, List<QueryUser> queryUserList) {
        this.context = context;
        this.queryUserList = queryUserList;
    }

    @NonNull
    @Override
    public UserChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleUserChatBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_user_chat, parent, false);
        return new UserChat(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChat holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return queryUserList.size();
    }

    public List<QueryUser> getQueryUserList() {
        return queryUserList;
    }

    class UserChat extends RecyclerView.ViewHolder {

        private final ViewSingleUserChatBinding mBinding;

        public UserChat(@NonNull ViewSingleUserChatBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;

            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.right_animation);
            itemView.setAnimation(animation);

        }

        private void setData(final int position) {

            final QueryUser queryUser = queryUserList.get(position);
            mBinding.TVLastMessageDate.setText(CommonUtils.getRelativeDate(queryUser.getLastMessageDate()));

            if (queryUser.getName() != null) {

                mBinding.TVUserName.setText(queryUser.getName());
            }

            if (queryUser.unreadMessagesAvailable()) {

                mBinding.TVTotalMessageCount.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_bg_primary));
                mBinding.TVTotalMessageCount.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                mBinding.TVTotalMessageCount.setText(String.valueOf(queryUser.getUnreadMessagesCount()));

            } else {
                mBinding.TVTotalMessageCount.setText(String.valueOf(queryUser.getTotalMessagesCount()));
            }

            mBinding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(context, SendChat.class);
                intent.putExtra(Chat.RECEIVING_USER_ID_KEY, queryUser.getUserID());
                if (queryUser.getName() != null) {
                    intent.putExtra(Chat.NAME_KEY, queryUser.getName());
                }
                CommonUtils.changeActivity((Activity) context, intent, false);

            });
        }
    }
}
