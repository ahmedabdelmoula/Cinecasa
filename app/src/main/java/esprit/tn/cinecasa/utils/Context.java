package esprit.tn.cinecasa.utils;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.entities.Actor;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.entities.TVShow;
import esprit.tn.cinecasa.entities.User;

/**
 * Created by ahmed on 01-Nov-17.
 */

public class Context {

    private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    public static User CURRENT_USER = new User();
    public static Movie BIG8_IMG;
    public static boolean SELECTED_TYPE = true;
    public static CenterFabActivity MAIN_ACTIVITY;
    public static User CONNECTED_USER = new User(1);
    public static Movie ITEM_MOVIE = new Movie();
    public static TVShow ITEM_TV_SHOW = new TVShow();
    public static Actor ITEM_ACTOR = new Actor();
    public static int ACTOR_ID;
    public static int selected = 0;
    public static boolean registerSelected = false;
    public static boolean FB_LOGIN = false;
}
