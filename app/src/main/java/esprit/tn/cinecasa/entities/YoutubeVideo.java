package esprit.tn.cinecasa.entities;

/**
 * Created by ahmed on 06-Nov-17.
 * class teb3a lel api youtube
 */

public class YoutubeVideo {
    String key;
    String size;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public YoutubeVideo(String key, String size) {
        this.key = key;
        this.size = size;
    }
}
