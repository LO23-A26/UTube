package common.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucas Dompnier
 * @version 1.0
 */
public class Index implements Serializable {

    private List<UserLight> user_list;
    private List<VideoLight> video_list;

    @Override
    public String toString() {
        return "Index{" +
                "user_list=" + user_list +
                ", video_list=" + video_list +
                '}';
    }

    public Index(){
        this.user_list = new ArrayList<>();
        this.video_list = new ArrayList<>();
    }

    public Index(List<UserLight> user_list, List<VideoLight> video_list) {
        this.user_list = user_list;
        this.video_list = video_list;
    }

    public void addVideo(VideoLight new_video){
        video_list.add(new_video);
    }

    public void addUser(UserLight new_user){
        user_list.add(new_user);
    }

    public List<UserLight> getUserList() {
        return user_list;
    }

    public void setUserList(List<UserLight> user_list) {
        this.user_list = user_list;
    }

    public List<VideoLight> getVideoList() {
        return video_list;
    }

    public void setVideoList(List<VideoLight> video_list) {
        this.video_list = video_list;
    }
}
