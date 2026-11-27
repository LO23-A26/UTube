package client.communication.implementations;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.UserLight;
import common.interfaces.comm.InterfaceIHMShareCallsComm;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Implementation of the InterfaceCommunicationCallsIHMShare interface.
 * This class is responsible for handling communication calls related to the IHMShare interface.
 */
public class IHMShareCallsComm implements InterfaceIHMShareCallsComm {

    private static final System.Logger logger = System.getLogger(IHMShareCallsComm.class.getName());

    private final CommClientController commClientController;

    // Constructor, takes in the comm client controller
    public IHMShareCallsComm(CommClientController commClientController) {
        this.commClientController = commClientController;
    }

    @Override
    public void addComment(Comment comment, UserLight owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addComment'");
    }

    @Override
    public void editComment(Comment comment, UserLight owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editComment'");
    }

    @Override
    public void deleteComment(Comment comment, UserLight owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteComment'");
    }

    @Override
    public void addRating(Rating rating, UserLight owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRating'");
    }

    @Override
    public void editRating(Rating rating, UserLight owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editRating'");
    }

    @Override
    public void deleteRating(Rating rating, UserLight owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRating'");
    }

    @Override
    public void askVideo(UUID videoId, UUID ownerId) {
        if (videoId == null || ownerId == null) {
            logger.log(ERROR, "Cannot download a video without its id or its owner");
            return;
        }
        commClientController.requestVideoDownloadFromClient(videoId, ownerId);
    }

    
}
