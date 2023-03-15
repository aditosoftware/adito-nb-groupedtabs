package de.adito.aditoweb.nbm.groupedtabs;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openide.loaders.DataObject;
import org.openide.util.Pair;
import org.openide.util.lookup.Lookups;
import org.openide.windows.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the {@link NbUtils} class
 *
 * @author p.neub, 13.03.2023
 */
class NbUtilsTest
{
  /**
   * Tests the {@link NbUtils#getTopComponentsInMode(TopComponent)} function
   */
  @Test
  void shouldReturnListOfTopComponentsInMode()
  {
    final TopComponent tc1inMode1 = mock(TopComponent.class);
    final TopComponent tc2InMode1 = mock(TopComponent.class);
    final Mode mode1 = mock(Mode.class);
    when(mode1.getTopComponents()).thenReturn(new TopComponent[]{tc1inMode1, tc2InMode1});

    final TopComponent tc1InMode2 = mock(TopComponent.class);
    final TopComponent tc2InMode2 = mock(TopComponent.class);
    final Mode mode2 = mock(Mode.class);

    final WindowManager windowManager = mock(WindowManager.class);
    //noinspection rawtypes,unchecked
    when(windowManager.getModes()).thenReturn((Set) Set.of(mode1, mode2));

    try (MockedStatic<WindowManager> windowManagerMockedStatic = mockStatic(WindowManager.class))
    {
      windowManagerMockedStatic.when(WindowManager::getDefault).thenReturn(windowManager);

      Set<TopComponent> tcsInMode1 = NbUtils.getTopComponentsInMode(tc2InMode1).collect(Collectors.toSet());
      assertEquals(Set.of(tc1inMode1, tc2InMode1), tcsInMode1);

      Set<TopComponent> tcsInMode2 = NbUtils.getTopComponentsInMode(tc1InMode2).collect(Collectors.toSet());
      assertEquals(Set.of(tc1InMode2, tc2InMode2), tcsInMode2);
    }
  }

  /**
   * Tests that {@link NbUtils#resolveDataObjects(TopComponent)} returns a non-empty stream
   * when a DataObject is in the TopComponents lookup.
   */
  @Test
  void shouldResolveDataObjectOfTopComponentWithDataObjectCorrectly()
  {
    final DataObject dataObject = mock(DataObject.class);
    final TopComponent tcWithDataObject = mock(TopComponent.class);
    when(tcWithDataObject.getLookup()).thenReturn(Lookups.fixed(dataObject));

    List<Pair<TopComponent, DataObject>> grouped = NbUtils.resolveDataObjects(tcWithDataObject).collect(Collectors.toList());
    assertEquals(List.of(Pair.of(tcWithDataObject, dataObject)), grouped);
  }

  /**
   * Tests that {@link NbUtils#resolveDataObjects(TopComponent)} returns a empty stream
   * when no DataObject is in the TopComponents lookup.
   */
  @Test
  void shouldResolveDataObjectOfTopComponentWithoutDataObjectCorrectly()
  {
    final TopComponent tcWithoutDataObject = mock(TopComponent.class);
    when(tcWithoutDataObject.getLookup()).thenReturn(Lookups.fixed());

    List<Pair<TopComponent, DataObject>> grouped = NbUtils.resolveDataObjects(tcWithoutDataObject).collect(Collectors.toList());
    assertEquals(List.of(), grouped);
  }
}
