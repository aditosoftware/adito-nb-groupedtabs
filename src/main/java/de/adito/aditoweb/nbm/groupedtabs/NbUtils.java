package de.adito.aditoweb.nbm.groupedtabs;

import lombok.*;
import org.openide.loaders.DataObject;
import org.openide.util.Pair;
import org.openide.windows.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

/**
 * Utillity class to interact with the NetBeans api used for grpuping tabs.
 *
 * @author p.neub, 13.03.2023
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NbUtils
{
  /**
   * Returns the currently active TopComponent.
   * This action should be used in Actions inside the TopComponents context menu,
   * for retrieving the target TopComponent.
   *
   * @return the currently active TopComponent
   */
  @NonNull
  public static TopComponent getActiveTopComponent()
  {
    // Get the current TopComponent, note that this is the currently active TopComponent,
    // not nessersarry the TopComponent that the context menu was opened on.
    // Currently, there is way to do this. The same problem also applies to the NetBeans Editors Action
    // see org.netbeans.core.multiview.EditorsAction.
    return WindowManager.getDefault().getRegistry().getActivated();
  }

  /**
   * Returns a Stream over all TopComponents that are in the same group as {@code pTcWithinMode}
   *
   * @param pTopComponentWithinMode a TopComponent
   * @return Stream of TopComponents inside the mode of {@code pTcWithinMode}
   */
  public static Stream<TopComponent> getTopComponentsInMode(@NonNull TopComponent pTopComponentWithinMode)
  {
    // get all TopCompoments from each group to find the mode containing pTopComponent
    return WindowManager.getDefault().getModes().stream()
        .map(Mode::getTopComponents)
        .map(Arrays::stream)

        // filter if TopComponents contain the current TopComponent
        .map(pTcs -> pTcs.collect(Collectors.toList()))
        .filter(pTcs -> pTcs.contains(pTopComponentWithinMode))

        // flatmap Stream<List<TopComponent>>
        .findFirst()
        .stream()
        .flatMap(List::stream);
  }

  /**
   * Returns a Stream that can be used with {@link Stream#flatMap(Function)} to group the DataObject to their corrosponding TopComponent.
   * TopComponents that have no DataObject associated with them return an empty stream.
   *
   * @param pTopComponent the TopComponent
   * @return stream with one pair of the TopComponent and its DataObject or an empty stream
   */
  public static Stream<Pair<TopComponent, DataObject>> resolveDataObjects(@NonNull TopComponent pTopComponent)
  {
    return Optional.ofNullable(pTopComponent.getLookup().lookup(DataObject.class))
        .map(pDataObject -> Pair.of(pTopComponent, pDataObject))
        .stream();
  }
}
