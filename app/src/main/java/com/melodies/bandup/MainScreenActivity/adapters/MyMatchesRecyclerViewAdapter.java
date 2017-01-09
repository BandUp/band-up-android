package com.melodies.bandup.MainScreenActivity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.melodies.bandup.MainScreenActivity.MatchesFragment.OnListFragmentInteractionListener;
import com.melodies.bandup.R;
import com.melodies.bandup.helper_classes.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMatchesRecyclerViewAdapter extends RecyclerView.Adapter<MyMatchesRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public MyMatchesRecyclerViewAdapter(Context context, List<User> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_matches, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).name);
        holder.mImageView.setImageBitmap(null);

        if (mValues.get(position).imgURL != null) {
            if (!mValues.get(position).imgURL.equals("")) {
                Picasso.with(mContext).load(mValues.get(position).imgURL).into(holder.mImageView);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_profile_picture_placeholder).into(holder.mImageView);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.ic_profile_picture_placeholder).into(holder.mImageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }
    public void addUser(User user) {
        this.mValues.add(user);
        this.notifyItemInserted(mValues.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.username);
            mImageView = (ImageView) view.findViewById(R.id.profile_picture);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
