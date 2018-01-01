package esprit.tn.cinecasa.utils;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Yessine on 11/27/2017.
 */

public class MultiSearch implements SearchSuggestion {

    private int id;
    private String name;
    private String mediaType;
    private String image;
    private String releaseDate;

    public MultiSearch(int id, String name, String mediaType, String image, String releaseDate) {
        this.id = id;
        this.name = name;
        this.mediaType = mediaType;
        this.image = image;
        this.releaseDate = releaseDate;
    }

    public MultiSearch(Parcel source) {
        this.name = source.readString();
        this.image = source.readString();
        this.releaseDate = source.readString();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String getBody() {
        String s = "";

        if (mediaType.equals("person"))
            s = name;
        else if (releaseDate.contains("-"))
            s = name + " (" + releaseDate.substring(0, releaseDate.indexOf('-')) + ")";

        return s;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static final Creator<MultiSearch> CREATOR = new Creator<MultiSearch>() {
        @Override
        public MultiSearch createFromParcel(Parcel in) {
            return new MultiSearch(in);
        }

        @Override
        public MultiSearch[] newArray(int size) {
            return new MultiSearch[size];
        }
    };
}
