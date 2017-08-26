package me.echeung.moemoekyun.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import me.echeung.moemoekyun.R;
import me.echeung.moemoekyun.api.v3.APIUtil;
import me.echeung.moemoekyun.api.v3.interfaces.FavoriteSongListener;
import me.echeung.moemoekyun.api.v3.interfaces.RequestSongListener;
import me.echeung.moemoekyun.api.v3.model.Song;
import me.echeung.moemoekyun.api.v3.ResponseMessages;
import me.echeung.moemoekyun.ui.fragments.UserFragment;
import me.echeung.moemoekyun.viewmodels.AppState;

public class SongActionsUtil {

    /**
     * Updates the favorite status of a song.
     *
     * @param song The song to update the favorite status of.
     */
    public static void favorite(final Activity activity, final RecyclerView.Adapter adapter, final Song song) {
        final WeakReference<Activity> activityReference = new WeakReference<>(activity);

        final int songId = song.getId();
        APIUtil.favoriteSong(activity, songId, new FavoriteSongListener() {
            @Override
            public void onFailure(final String result) {
                Activity act = activityReference.get();
                if (act != null) {
                    act.runOnUiThread(() -> Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onSuccess(final boolean favorited) {
                if (AppState.getInstance().currentSong.get().getId() == songId) {
                    AppState.getInstance().setFavorited(favorited);
                }

                Activity act = activityReference.get();
                if (act != null) {
                    act.runOnUiThread(() -> {
                        song.setFavorite(favorited);
                        adapter.notifyDataSetChanged();

                        // Broadcast change
                        final Intent favIntent = new Intent(UserFragment.FAVORITE_EVENT);
                        act.sendBroadcast(favIntent);

                        // Undo action
                        if (!favorited) {
                            final View coordinatorLayout = act.findViewById(R.id.coordinator_layout);
                            if (coordinatorLayout != null) {
                                final Snackbar undoBar = Snackbar.make(coordinatorLayout,
                                        String.format(activity.getString(R.string.unfavorited), song.getTitle()),
                                        Snackbar.LENGTH_LONG);
                                undoBar.setAction(R.string.action_undo, (v) -> favorite(activity, adapter, song));
                                undoBar.show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Requests a song.
     *
     * @param song The song to request.
     */
    public static void request(final Activity activity, final RecyclerView.Adapter adapter, final Song song) {
        final WeakReference<Activity> activityReference = new WeakReference<>(activity);

        APIUtil.requestSong(activity, song.getId(), new RequestSongListener() {
            @Override
            public void onFailure(final String result) {
                Activity act = activityReference.get();
                if (act != null) {
                    act.runOnUiThread(() -> {
                        final int message = result.equals(ResponseMessages.USER_NOT_SUPPORTER) ?
                                R.string.supporter_required :
                                R.string.error;

                        Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onSuccess() {
                Activity act = activityReference.get();
                if (act != null) {
                    act.runOnUiThread(() -> {
                        Toast.makeText(act, R.string.success, Toast.LENGTH_LONG).show();

                        song.setEnabled(false);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }
}