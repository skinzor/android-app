package me.echeung.moemoekyun.client.socket.response;

import java.util.List;

import lombok.Getter;
import me.echeung.moemoekyun.client.model.Event;
import me.echeung.moemoekyun.client.model.Song;
import me.echeung.moemoekyun.client.model.User;

@Getter
public class UpdateResponse extends BaseResponse {
    private String t;
    private Details d;

    @Getter
    public static class Details {
        private Song song;
        private String startTime;
        private List<Song> lastPlayed;
        private Queue queue;
        private int listeners;
        private User requester;
        private Event event;
    }

    @Getter
    public static class Queue {
        private int inQueue;
        private int inQueueByUser;
        private int inQueueBeforeUser;
    }
}
