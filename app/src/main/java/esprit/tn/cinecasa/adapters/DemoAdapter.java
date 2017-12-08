package esprit.tn.cinecasa.adapters;

import android.widget.ListAdapter;


import java.util.List;

import esprit.tn.cinecasa.utils.DemoItem;

public interface DemoAdapter extends ListAdapter {

  void appendItems(List<DemoItem> newItems);

  void setItems(List<DemoItem> moreItems);
}
