package esprit.tn.cinecasa.entities;

/**
 * Created by Yessine on 11/25/2017.
 */

public class Actor {

    private int id;
    private String birthday;
    private String deathday;
    private String[] aka;
    private int gender;
    private String biography;
    private Double popularity;
    private String placeOfBirth;
    private String image;
    private boolean adult;
    private String imdbId;
    private String homePage;
    private String name;

    public Actor(int id) {
        this.id = id;
    }

    public Actor(){

    }

    public Actor(int id, String birthday, String deathday, int gender, String biography, Double popularity, String placeOfBirth, String image, boolean adult, String imdbId, String homePage, String name) {
        this.id = id;
        this.birthday = birthday;
        this.deathday = deathday;
        this.aka = aka;
        this.gender = gender;
        this.biography = biography;
        this.popularity = popularity;
        this.placeOfBirth = placeOfBirth;
        this.image = image;
        this.adult = adult;
        this.imdbId = imdbId;
        this.homePage = homePage;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public String[] getAka() {
        return aka;
    }

    public void setAka(String[] aka) {
        this.aka = aka;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
