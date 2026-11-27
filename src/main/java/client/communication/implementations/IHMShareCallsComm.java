package client.communication.implementations;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.UserLight;
import common.dataclasses.communication.ClientMessage;
import common.dataclasses.communication.messages.AddCommentMessage;
import common.dataclasses.communication.messages.AddRatingMessage;
import common.dataclasses.communication.messages.DeleteCommentMessage;
import common.dataclasses.communication.messages.DeleteRatingMessage;
import common.dataclasses.communication.messages.EditCommentMessage;
import common.dataclasses.communication.messages.EditRatingMessage;
import common.interfaces.comm.InterfaceIHMShareCallsComm;

/**
 * Implementation of the InterfaceCommunicationCallsIHMShare interface.
 * This class is responsible for handling communication calls related to the IHMShare interface.
 */
public class IHMShareCallsComm implements InterfaceIHMShareCallsComm {

    @SuppressWarnings("unused")
    private final CommClientController commClientController;

    // Constructor, takes in the comm client controller
    public IHMShareCallsComm(CommClientController commClientController) {
        this.commClientController = commClientController;
    }

    @Override
    public void addComment(Comment comment, UserLight owner) {
        ClientMessage message = new AddCommentMessage(comment);
        commClientController.clientConnectionsManager.sendToClient(owner.getUuid(), message);   
    }

    @Override
    public void editComment(Comment comment, UserLight owner) {
        ClientMessage message = new EditCommentMessage(comment);
        commClientController.clientConnectionsManager.sendToClient(owner.getUuid(), message);   
    }

    @Override
    public void deleteComment(Comment comment, UserLight owner) {
        ClientMessage message = new DeleteCommentMessage(comment);
        commClientController.clientConnectionsManager.sendToClient(owner.getUuid(), message);   
    }

    @Override
    public void addRating(Rating rating, UserLight owner) {
        ClientMessage message = new AddRatingMessage(rating);
        commClientController.clientConnectionsManager.sendToClient(owner.getUuid(), message);
    }

    @Override
    public void editRating(Rating rating, UserLight owner) {
        ClientMessage message = new EditRatingMessage(rating);
        commClientController.clientConnectionsManager.sendToClient(owner.getUuid(), message);
    }

    @Override
    public void deleteRating(Rating rating, UserLight owner) {
        ClientMessage message = new DeleteRatingMessage(rating);
        commClientController.clientConnectionsManager.sendToClient(owner.getUuid(), message);
    }

    @Override
    public void askVideo(UUID videoId, UUID ownerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'askVideo'");
    }

    
}
