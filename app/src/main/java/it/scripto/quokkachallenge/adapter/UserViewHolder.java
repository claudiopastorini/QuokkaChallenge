/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Claudio Pastorini
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package it.scripto.quokkachallenge.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.scripto.quokkachallenge.R;
import it.scripto.quokkachallenge.model.User;

/**
 *
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    /**
     * Name TextView.
     */
    @Bind(R.id.user_name)
    protected TextView userNameTextView;

    /**
     * Friends count TextView.
     */
    @Bind(R.id.user_friends_count)
    protected TextView friendsCountTextView;

    /**
     * Agency ImageView.
     */
    @Bind(R.id.user_picture)
    protected ImageView userPictureImageView;

    /**
     * OnCardClickListener.
     */
    private OnCardClickListener onCardClickListener;

    /**
     * User.
     */
    private User user;

    /**
     * Constructor for the ViewHolder.
     *
     * @param view                the view to fill with data.
     * @param onCardClickListener the click listener to set.
     */
    public UserViewHolder(View view, OnCardClickListener onCardClickListener) {
        super(view);

        // Binds views
        ButterKnife.bind(this, view);

        // Sets the clickListener
        this.onCardClickListener = onCardClickListener;
    }

    // OnCardClick listener called
    @OnClick(R.id.user_picture)
    void onClick(View view) {
        onCardClickListener.onClick(view, user);
    }

    /**
     * Binds User to the view.
     *
     * @param context the context.
     * @param user    the User to bind to the view.
     */
    public void bind(Context context, final User user) {

        // Sets User
        this.user = user;

        // Sets name
        userNameTextView.setText(user.getName());

        // Sets friends count
        friendsCountTextView.setText(String.valueOf(user.getFriends().size()));

        // Sets picture
        Picasso.with(context)
                .load(user.getPicture())
                .placeholder(R.color.accent_color)
                .fit()
                .centerInside()
                .into(userPictureImageView);
    }
}