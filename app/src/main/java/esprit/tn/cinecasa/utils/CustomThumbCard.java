/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package esprit.tn.cinecasa.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * This class provides an example of custom thumbnail card.
 * <p/>
 * You have to override the {@link #setupInnerViewElements(android.view.ViewGroup, android.view.View)});
 * </p>
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class CustomThumbCard extends CardThumbnail {

    private int baseW = 160;
    private int baseH = 220;
    private int pos;

    public CustomThumbCard(Context context, int pos) {
        super(context);
        this.pos = pos;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View viewImage) {
        if (viewImage != null) {

            if (parent != null && parent.getResources() != null) {
                DisplayMetrics metrics = parent.getResources().getDisplayMetrics();

                if (pos == 0) {
                    baseH= 320;
                    viewImage.getLayoutParams().width = metrics.widthPixels;
                    viewImage.getLayoutParams().height = (int) (baseH * metrics.density);
                } else {
                    viewImage.getLayoutParams().width = (int) (baseW * metrics.density);
                    viewImage.getLayoutParams().height = (int) (baseH * metrics.density);
                }
            }
        }
    }
}
