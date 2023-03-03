package de.adito.aditoweb.nbm.groupedtabs.actions;

import de.adito.nbm.groupedtabs.api.IDataObjectGroupProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.Lookups;
import org.openide.windows.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests methods inside the {@link SelectGroupAction} class and its subclasses
 *
 * @author p.neub, 02.03.2023
 */
class SelectGroupActionTest
{
  /**
   * Tests if {@link SelectGroupAction.GroupingMenu#getGroupsInTopComponentMode(TopComponent)}
   * can correctly get a sorted list of groups from the currently opened TopComponents.
   */
  @Test
  void shouldGetAllCurrentlyUsedGroupsCorrectly()
  {
    try (final MockedStatic<WindowManager> mockedStaticWm = mockStatic(WindowManager.class))
    {
      final DataObject dataObjectAAA = mock(DataObject.class);
      final TopComponent tcAAA = mock(TopComponent.class);
      when(tcAAA.isOpened()).thenReturn(true);
      when(tcAAA.getLookup()).thenReturn(Lookups.fixed(dataObjectAAA));

      final DataObject dataObjectBBB = mock(DataObject.class);
      final TopComponent tcBBB = mock(TopComponent.class);
      when(tcBBB.isOpened()).thenReturn(true);
      when(tcBBB.getLookup()).thenReturn(Lookups.fixed(dataObjectBBB));

      final TopComponent tcHidden = mock(TopComponent.class);
      when(tcHidden.isOpened()).thenReturn(false);

      final DataObject dataObjectNone = mock(DataObject.class);
      final TopComponent tcNone = mock(TopComponent.class);
      when(tcNone.isOpened()).thenReturn(true);
      when(tcNone.getLookup()).thenReturn(Lookups.fixed(dataObjectNone));

      final TopComponent[] topComponents = new TopComponent[]{tcHidden, tcBBB, tcNone, tcAAA};

      final Mode mockedMode = mock(Mode.class);
      when(mockedMode.getTopComponents()).thenReturn(topComponents);

      final WindowManager mockedWm = mock(WindowManager.class);
      //noinspection rawtypes,unchecked
      when(mockedWm.getModes()).thenReturn((Set) Set.of(mockedMode));

      mockedStaticWm.when(WindowManager::getDefault).thenReturn(mockedWm);

      try (final MockedStatic<IDataObjectGroupProvider> mockedStaticGroupProvider = mockStatic(IDataObjectGroupProvider.class))
      {
        final IDataObjectGroupProvider mockedGroupProvider = mock(IDataObjectGroupProvider.class);
        when(mockedGroupProvider.group(dataObjectAAA)).thenReturn(Optional.of("AAA"));
        when(mockedGroupProvider.group(dataObjectBBB)).thenReturn(Optional.of("BBB"));
        when(mockedGroupProvider.group(dataObjectNone)).thenReturn(Optional.empty());

        mockedStaticGroupProvider.when(IDataObjectGroupProvider::getDefault).thenReturn(mockedGroupProvider);

        List<String> groups = SelectGroupAction.GroupingMenu.getGroupsInTopComponentMode(tcBBB)
            .collect(Collectors.toList());

        List<String> expected = List.of("AAA", "BBB");
        assertEquals(expected, groups);
      }
    }
  }
}
