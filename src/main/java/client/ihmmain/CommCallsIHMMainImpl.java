package client.ihmmain;

import common.dataclasses.UserFull;
import common.interfaces.IHMMain.InterfaceCommCallsIHMMain;
import javafx.application.Platform;

import java.util.Objects;
import java.util.UUID;

/** Receives COM display requests on the JavaFX thread. */
public final class CommCallsIHMMainImpl implements InterfaceCommCallsIHMMain {
    private final IHMMainManager manager;

    public CommCallsIHMMainImpl(IHMMainManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @Override public void displayRemoteProfile(UserFull profile) {
        Platform.runLater(() -> manager.displayRemoteProfile(profile));
    }

    @Override public void removeVideoFromList(UUID id) {
        Platform.runLater(() -> manager.removeVideo(id));
    }
}
