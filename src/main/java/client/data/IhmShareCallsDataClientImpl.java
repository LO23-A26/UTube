package client.data;

import common.dataclasses.*;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;

import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

/**
 Implementation of the IHM Share calls Dataclient interface
 *
 * @author Alexandre Lajeunesse
 * @version 1.0
 */

public class IhmShareCallsDataClientImpl implements InterfaceIhmShareCallsDataClient {

    private final DataClientCore data_client_core;

    public IhmShareCallsDataClientImpl(DataClientCore data_client_core) {
        this.data_client_core = data_client_core;
    }

    @Override
    public void addVideo(Path file, String title, String creator, Set<String> tags) {
        data_client_core.addingNewVideo(file, title, creator, tags);
    }

    // Pas encore implémentées, mais ça va probablement venir à un moment
    @Override public void sendDetailsRequest(UUID video_id) {}
    @Override public UserLight getUser() { return null; }
    @Override public VideoFull getVideo(VideoLight video) { return null; }
    @Override public void deleteComment(UUID video_id, UUID author_uid) {}
    @Override public void deleteRating(UUID video_id, UUID author_uid) {}
}