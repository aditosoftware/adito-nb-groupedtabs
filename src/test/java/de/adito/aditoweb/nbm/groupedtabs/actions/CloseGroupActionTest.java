package de.adito.aditoweb.nbm.groupedtabs.actions;

import de.adito.nbm.groupedtabs.api.IDataObjectGroupProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.Lookups;
import org.openide.windows.*;

import java.awt.event.ActionEvent;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Tests the {@link CloseGroupAction} class
 *
 * @author p.neub, 13.03.2023
 */
class CloseGroupActionTest
{
  /**
   * Checks that the {@link CloseGroupAction} closes all TopComponents
   * that are in the same Mode as the currently active TopCompoient
   */
  @Test
  void shouldCloseAllTopComponentsInGroup()
  {
    final DataObject tc1InMode1Do = mock(DataObject.class);
    final TopComponent tc1InMode1 = mock(TopComponent.class);
    when(tc1InMode1.getLookup()).thenReturn(Lookups.fixed(tc1InMode1Do));

    final Mode mode1 = mock(Mode.class);
    when(mode1.getTopComponents()).thenReturn(new TopComponent[]{tc1InMode1});


    final DataObject tc1InMode2Do = mock(DataObject.class);
    final TopComponent tc1InMode2 = mock(TopComponent.class);
    when(tc1InMode2.getLookup()).thenReturn(Lookups.fixed(tc1InMode2Do));

    final DataObject tc2InMode2Do = mock(DataObject.class);
    final TopComponent tc2InMode2 = mock(TopComponent.class);
    when(tc2InMode2.getLookup()).thenReturn(Lookups.fixed(tc2InMode2Do));

    final DataObject tc3InMode2Do = mock(DataObject.class);
    final TopComponent tc3InMode2 = mock(TopComponent.class);
    when(tc3InMode2.getLookup()).thenReturn(Lookups.fixed(tc3InMode2Do));

    final Mode mode2 = mock(Mode.class);
    when(mode2.getTopComponents()).thenReturn(new TopComponent[]{tc1InMode2, tc2InMode2, tc3InMode2});


    final TopComponent.Registry registry = mock(TopComponent.Registry.class);
    when(registry.getActivated()).thenReturn(tc2InMode2);

    final WindowManager windowManager = mock(WindowManager.class);
    when(windowManager.getRegistry()).thenReturn(registry);
    //noinspection rawtypes,unchecked
    when(windowManager.getModes()).thenReturn((Set) Set.of(mode1, mode2));

    try (MockedStatic<WindowManager> windowManagerMockedStatic = mockStatic(WindowManager.class))
    {
      windowManagerMockedStatic.when(WindowManager::getDefault).thenReturn(windowManager);

      final IDataObjectGroupProvider groupProvider = mock(IDataObjectGroupProvider.class);
      when(groupProvider.group(tc1InMode1Do)).thenReturn(Optional.of("AAA"));
      when(groupProvider.group(tc1InMode2Do)).thenReturn(Optional.of("AAA"));
      when(groupProvider.group(tc2InMode2Do)).thenReturn(Optional.of("AAA"));
      when(groupProvider.group(tc3InMode2Do)).thenReturn(Optional.of("BBB"));

      try (MockedStatic<IDataObjectGroupProvider> groupProviderMockedStatic = mockStatic(IDataObjectGroupProvider.class))
      {
        groupProviderMockedStatic.when(IDataObjectGroupProvider::getDefault).thenReturn(groupProvider);

        final ActionEvent eventMock = mock(ActionEvent.class);
        final CloseGroupAction action = new CloseGroupAction();
        action.actionPerformed(eventMock);
        verifyNoInteractions(eventMock);

        verify(tc1InMode1, times(0)).close();
        verify(tc1InMode2, times(1)).close();
        verify(tc2InMode2, times(1)).close();
        verify(tc3InMode2, times(0)).close();
      }
    }
  }
}
