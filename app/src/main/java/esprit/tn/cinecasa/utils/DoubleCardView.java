package esprit.tn.cinecasa.utils;

import android.support.v7.widget.CardView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Yessine on 11/20/2017.
 */

public class DoubleCardView {

    private Card first;
    private Card second;

    public DoubleCardView(Card first, Card second) {
        this.first = first;
        this.second = second;
    }

    public Card getFirst() {
        return first;
    }

    public void setFirst(Card first) {
        this.first = first;
    }

    public Card getSecond() {
        return second;
    }

    public void setSecond(Card second) {
        this.second = second;
    }
}
