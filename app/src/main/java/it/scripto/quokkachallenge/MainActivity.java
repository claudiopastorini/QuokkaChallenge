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

package it.scripto.quokkachallenge;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.scripto.quokkachallenge.adapter.EmptySupportRecyclerView;
import it.scripto.quokkachallenge.adapter.OnCardClickListener;
import it.scripto.quokkachallenge.adapter.UserAdapter;
import it.scripto.quokkachallenge.api.QuokkaApiService;
import it.scripto.quokkachallenge.model.User;
import it.scripto.quokkachallenge.util.BaseActivityWithToolbar;
import it.scripto.quokkachallenge.util.ConnectionHelper;
import it.scripto.quokkachallenge.util.ConnectionReceiver;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends BaseActivityWithToolbar implements ConnectionHelper.Callbacks {

    /**
     * Base url for API service.
     */
    private static final String BASE_URL = "http://www.quokka-app.com";
    @Bind(R.id.users_recycler_view)
    protected EmptySupportRecyclerView usersRecyclerView;
    @Bind(R.id.empty_users_view)
    protected LinearLayout emptyUsersView;
    @Bind(R.id.empty_users_text_view)
    protected TextView emptyUsersTextView;
    @Bind(R.id.touch_interceptor_view)
    protected View touchInterceptorView;
    @Bind(R.id.details_layout)
    protected LinearLayout detailsLayout;
    @Bind(R.id.unfoldable_view)
    protected UnfoldableView unfoldableView;
    @Bind(R.id.details_user_picture)
    protected ImageView detailsUserPictureImageView;
    @Bind(R.id.details_user_name)
    protected TextView detailsUserNameTextView;
    @Bind(R.id.details_user_age)
    protected TextView detailsUserAgeTextView;
    @Bind(R.id.details_user_friends)
    protected TextView detailsUserFriendsTextView;
    @Bind(R.id.details_user_description)
    protected TextView detailsUserDescriptionTextView;
    @Bind(R.id.progress_bar)
    protected ProgressWheel progressBar;
    @Bind(R.id.try_again_button)
    protected Button tryAgainButton;
    /**
     * Api service.
     */
    private QuokkaApiService quokkaApiService;
    /**
     * User adapter bound to RecyclerView.
     */
    private UserAdapter userAdapter;
    /**
     * BroadcastReceiver useful in order to listen for connection changes.
     */
    private ConnectionReceiver connectionReceiver;

    @OnClick(R.id.try_again_button)
    void onClick(View view) {
        getUsers();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate in order to set the right theme and remove the launcher theme
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        // Binds views
        ButterKnife.bind(this);

        // Removes back arrow enable by default from BaseActivityWithToolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Remove up arrow
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Create BroadcastReceiver for connectivity checking
        connectionReceiver = new ConnectionReceiver();

        /*
        In modern Android UIs developers should lean more on a visually distinct color scheme for
        toolbars than on their application icon. The use of application icon plus title as a
        standard layout is discouraged on API 21 devices and newer.
        From: http://developer.android.com/reference/android/support/v7/widget/Toolbar.html
        */
        //noinspection ConstantConditions
        getSupportActionBar().setLogo(R.drawable.ic_logo);

        // Creates service
        createQuokkaService();

        // Prepares recycler view
        prepareRecyclerView();
    }

    @Override
    public void onBackPressed() {
        // Overrides in order to unfold when back button is pressed if foldable view is shown
        if (unfoldableView != null && (unfoldableView.isUnfolded() || unfoldableView.isUnfolding())) {
            unfoldableView.foldBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start connectivity checker
        connectionReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop connectivity checker
        connectionReceiver.unRegister(this);
    }

    /**
     * Creates Retrofit's QuokkaService.
     */
    private void createQuokkaService() {
        // Create Retrofit service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        quokkaApiService = retrofit.create(QuokkaApiService.class);
    }

    /**
     * Prepare RecyclerView with empty view and foldable view.
     */
    private void prepareRecyclerView() {
        // Sets layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);

        // Sets empty view
        usersRecyclerView.setEmptyView(emptyUsersView);

        // Sets adapter
        userAdapter = new UserAdapter(this);
        usersRecyclerView.setAdapter(userAdapter);

        // Sets on card click listener
        userAdapter.setOnCardClickListener(new OnCardClickListener() {
            @Override
            public void onClick(View view, User user) {

                // Sets name
                detailsUserNameTextView.setText(user.getName());

                // Sets description
                detailsUserDescriptionTextView.setText(user.getDescription());

                // Sets age
                detailsUserAgeTextView.setText(String.valueOf(user.getAge()));

                // Sets number of friends
                detailsUserFriendsTextView.setText(String.valueOf(user.getFriends().size()));

                // Sets picture
                Picasso.with(getParent())
                        .load(user.getPicture())
                        .placeholder(R.color.accent_color)
                        .fit()
                        .centerInside()
                        .into(detailsUserPictureImageView);

                unfoldableView.unfold(view, detailsLayout);
            }
        });

        // Sets unfoldable stuffs
        touchInterceptorView.setClickable(false);

        detailsLayout.setVisibility(View.INVISIBLE);

        unfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(true);
                detailsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(false);
                detailsLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Retrieves users.
     */
    private void getUsers() {
        Log.i(TAG, "getUsers called");

        // Unhide progress bar
        progressBar.setVisibility(View.VISIBLE);
        // Hide empty view
        emptyUsersView.setVisibility(View.GONE);

        // Creates asynchronous call
        Call<List<User>> users = quokkaApiService.listUsers();
        users.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Response<List<User>> response, Retrofit retrofit) {

                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                if (response.isSuccess() && response.body() != null) {

                    // Gets response's body and show the result
                    List<User> responseUserList = response.body();
                    showUsers(responseUserList);

                } else {

                    // Unhide empty view
                    emptyUsersView.setVisibility(View.VISIBLE);
                    emptyUsersTextView.setText(R.string.empty_users_recycler_view);
                    tryAgainButton.setVisibility(View.VISIBLE);

                    try {
                        Log.e(TAG, String.format("Response not succeed in getArticlesBy: %s", response.errorBody().string()));
                    } catch (IOException e) {
                        Log.e(TAG, "Response not succeed in getArticlesBy, it could not get string of errorBody()", e);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Unhide empty view
                emptyUsersView.setVisibility(View.VISIBLE);
                emptyUsersTextView.setText(R.string.empty_users_recycler_view);
                tryAgainButton.setVisibility(View.VISIBLE);

                Log.e(TAG, "Error in onFailure in getUsers", t);
            }
        });
    }

    /**
     * Shows user by RecyclerView.
     */
    private void showUsers(List<User> users) {
        userAdapter.addUsers(users);
    }

    @Override
    public void onConnectionChanged(boolean isConnected, boolean isOnline) {

        Log.i(TAG, String.format("Connection changed, now is connected %s and is online %s", isConnected, isOnline));

        if (userAdapter.getUserList().size() == 0) {
            if (!isConnected) {
                // Shows no connection error
                emptyUsersTextView.setText(R.string.connection_error);
                tryAgainButton.setVisibility(View.INVISIBLE);
            } else if (!isOnline) {
                // Shows captive error
                emptyUsersTextView.setText(R.string.captive_error);
                tryAgainButton.setVisibility(View.INVISIBLE);
            } else {
                getUsers();
            }
        }
    }
}