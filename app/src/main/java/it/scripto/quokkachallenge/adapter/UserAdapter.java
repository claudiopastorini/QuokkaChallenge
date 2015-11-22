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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.scripto.quokkachallenge.R;
import it.scripto.quokkachallenge.model.User;

/**
 * Adapter for Users.
 */
public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    /**
     * Layout inflater.
     */
    private final LayoutInflater layoutInflater;

    /**
     * Users list bound to the adapter.
     */
    private final List<User> UserList;

    /**
     * Context.
     */
    private Context context;

    /**
     * OnCardClickListener.
     */
    private OnCardClickListener onCardClickListener;

    public UserAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        // Create a new User list, use addUsers() or addUser() in order to add User in the adapter.
        this.UserList = new ArrayList<>();
    }

    /**
     * Sets onClickListener of the card.
     *
     * @param onCardClickListener the onCardClickListener to set.
     */
    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View UserCard = layoutInflater.inflate(R.layout.card_user, parent, false);
        return new UserViewHolder(UserCard, onCardClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final User user = this.UserList.get(position);
        holder.bind(context, user);
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }

    /**
     * It will animate between the List of objects currently displayed in the Adapter to the
     * filtered List.
     *
     * @param newUserList the new users list.
     */
    public void animateTo(List<User> newUserList) {
        if (this.UserList.containsAll(newUserList)) {
            applyAndAnimateRemovals(newUserList);
            applyAndAnimateAdditions(newUserList);
            applyAndAnimateMovedUsers(newUserList);
        } else {
            addUsers(newUserList);
        }
    }

    /**
     * This method iterates through the internal List of the Adapter backwards and checks if each
     * item is contained in the new filtered List. If it is not it calls removeItem(). The reason
     * we iterate backwards is to avoid having to keep track of an offset. If you remove an item all
     * items below it move up. If you iterate through to the List from the bottom up then only items
     * which you have already iterated over are moved.
     *
     * @param newUserList the new users list.
     */
    private void applyAndAnimateRemovals(List<User> newUserList) {
        for (int i = this.UserList.size() - 1; i >= 0; i--) {
            final User model = this.UserList.get(i);
            if (!newUserList.contains(model)) {
                removeUser(i);
            }
        }
    }

    /**
     * This method iterating through the internal List of the Adapter it iterates through the
     * filtered List and checks if the item exists in the internal List.
     * If it does not it calls addItem().
     *
     * @param newUserList the new users list.
     */
    private void applyAndAnimateAdditions(List<User> newUserList) {
        for (int i = 0, count = newUserList.size(); i < count; i++) {
            final User User = newUserList.get(i);
            if (!this.UserList.contains(User)) {
                addUser(i, User);
            }
        }
    }

    /**
     * This method is essentially a combination of applyAndAnimateRemovals() and
     * applyAndAnimateAdditions() but with a twist. What does is it iterates through the filtered
     * List backwards and looks up the index of each item in the internal List.
     * If it detects a difference in the index it calls moveItem() to bring the internal List of the
     * Adapter in line with the filtered List.
     * <p/>
     * To call after applyAndAnimateRemovals() and applyAndAnimateAdditions().
     *
     * @param newUserList the new Users list.
     */
    private void applyAndAnimateMovedUsers(List<User> newUserList) {
        for (int toPosition = newUserList.size() - 1; toPosition >= 0; toPosition--) {
            final User model = newUserList.get(toPosition);
            final int fromPosition = this.UserList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveUser(fromPosition, toPosition);
            }
        }
    }

    /**
     * Removes item from a certain position.
     *
     * @param position the position in which there is the User to remove.
     * @return the user removed.
     */
    public User removeUser(int position) {
        final User model = this.UserList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    /**
     * Adds User to a certain position.
     *
     * @param position position in which add User.
     * @param User     User to add.
     */
    public void addUser(int position, User User) {
        this.UserList.add(position, User);
        notifyItemInserted(position);
    }

    /**
     * Adds Users to adapter.
     *
     * @param UserList the list to add.
     */
    public void addUsers(List<User> UserList) {
        this.UserList.addAll(UserList);
        notifyItemRangeInserted(this.UserList.size() - UserList.size(), UserList.size());
    }

    /**
     * Moves User from a position to another one.
     *
     * @param fromPosition position where to get the User.
     * @param toPosition   position where to put the User.
     */
    public void moveUser(int fromPosition, int toPosition) {
        final User model = this.UserList.remove(fromPosition);
        this.UserList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Gets the users list showed.
     *
     * @return the list of user showed
     */
    public List<User> getUserList() {
        return this.UserList;
    }
}