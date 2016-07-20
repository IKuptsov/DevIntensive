package com.softdesign.devintensive.ui.adapters;

import android.app.LoaderManager;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>  {


    private static final String TAG = ConstantManager.TAG_PREFIX + "UserAdapter";
    private Context mContext;
    private List<User> mUsers;
    private UserViewHolder.CustomClickListener mCustomClickListener;

    public UserAdapter(List<User> users, UserViewHolder.CustomClickListener customClickListener) {
        mUsers = users;
        mCustomClickListener = customClickListener;
    }

    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }


    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {

        final User user = mUsers.get(position);
        final String userPhoto;

        if (user.getPhoto().isEmpty()) {
            userPhoto = "null";
            Log.e(TAG, "onBindViewHolder: user with name " + user.getFullName() + " has empty name");
        } else {
            userPhoto = user.getPhoto();
        }

        DataManager.getINSTANCE().getPicasso()
                .load(userPhoto)
                .error(holder.dummy)
                .placeholder(holder.dummy)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.userPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getINSTANCE().getPicasso()
                                .load(userPhoto)
                                .error(holder.dummy)
                                .placeholder(holder.dummy)
                                .fit()
                                .centerCrop()
                                .into(holder.userPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "Грузит фотку с инета, черт");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "could't fetch image");
                                    }
                                });
                    }
                });

        holder.mfullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected AspectRatioImageView userPhoto;
        protected TextView mfullName, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;
        private CustomClickListener mListener;
        private Drawable dummy;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;
            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo_img);
            mfullName = (TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView) itemView.findViewById(R.id.projects_txt);
            mBio = (TextView) itemView.findViewById(R.id.bio_txt);
            dummy = userPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_button);
            mShowMore.setOnClickListener(this);
        }

        public interface CustomClickListener {
            void onUserItemClickListener(int position);
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }

        }

    }



}
