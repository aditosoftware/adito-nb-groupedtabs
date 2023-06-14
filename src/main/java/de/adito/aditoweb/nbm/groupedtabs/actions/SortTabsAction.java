package de.adito.aditoweb.nbm.groupedtabs.actions;

import de.adito.aditoweb.nbm.groupedtabs.*;
import de.adito.nbm.groupedtabs.api.IDataObjectGroupProvider;
import lombok.NonNull;
import org.openide.awt.*;
import org.openide.loaders.DataObject;
import org.openide.util.Pair;
import org.openide.windows.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.*;

/**
 * Action that sorts all tabs in the current {@link Mode}.
 *
 * @author p.neub, 28.02.2023
 */
@ActionID(category = "Window/SelectDocumentNode", id = "de.adito.aditoweb.nbm.groupedtabs.actions.SortTabsAction")
@ActionRegistration(displayName = "#LBL_SortTabs_Action")
@ActionReferences({
    @ActionReference(path = "Editors/TabActions", position = -10000, separatorAfter = -10000 + 1),
    @ActionReference(path = "Shortcuts", name = "DA-S")
})
public final class SortTabsAction extends AbstractAction
{
  @Override
  public void actionPerformed(ActionEvent e)
  {
    // get all modes (a mode is a group of tabs)
    WindowManager.getDefault().getModes().stream()
        // only editor modes (e.g. we don't want to reorder output tabs)
        .filter(pMode -> "editor".equals(pMode.getName()))
        .forEach(pMode -> {
          // get all openend tabs of that mode and sort them
          List<TopComponent> tcs = getSortedOpenedTopComponents(pMode.getTopComponents());

          // iterate through the TopComponents and open them at their sorted position
          // if the TopComponent is allready opened (whitch is the case for us) NetBeans will just move the TopComponent
          for (int i = 0; i < tcs.size(); i++)
            tcs.get(i).openAtTabPosition(i);
        });
  }

  /**
   * Sorts the provided TopComponents using the default {@link IDataObjectGroupProvider}.
   * It also only returns the currently opened TopComponents.
   *
   * @param pTopComponents array of TopComponents that should be sorted
   * @return sorted list of all opened TopComponents provided through {@code pTopComponents}
   */
  @NonNull
  static List<TopComponent> getSortedOpenedTopComponents(@NonNull TopComponent[] pTopComponents)
  {
    final IDataObjectGroupProvider groupProvider = IDataObjectGroupProvider.getDefault();

    // stream over all opened TopComponents
    return Arrays.stream(pTopComponents)
        .filter(TopComponent::isOpened)

        // get the corrosponding dataobject
        .map(pTc -> Pair.of(pTc, Optional.ofNullable(pTc.getLookup().lookup(DataObject.class))))

        // group by the group of the TopComponent
        .collect(Collectors.groupingBy(pPair -> Optional.ofNullable((String) pPair.first().getClientProperty(Group.PROP_GROUP))
            .or(() -> pPair.second().flatMap(groupProvider::group))))

        // get entries as keyvalue pairs and stream over them
        .entrySet()
        .stream()

        // sort by group name, groups that use the default group (null) come last
        .sorted(Comparator.comparing(pEntry -> pEntry.getKey().orElse(null), Comparator.nullsLast(String::compareTo)))

        // flatmap tabs inside each group sorted by the groupProvider
        .flatMap(pEntry -> pEntry.getValue().stream().sorted(
            Comparator.comparing(pPair -> pPair.second().orElse(null), Comparator.nullsLast(groupProvider))))

        // only get the TopComponents and collect them into a list
        .map(Pair::first)
        .collect(Collectors.toList());
  }
}
