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
    final DataObject firstTcInFirstModeDo = mock(DataObject.class);
    final TopComponent firstTcInFirstMode = mock(TopComponent.class);
    when(firstTcInFirstMode.getLookup()).thenReturn(Lookups.fixed(firstTcInFirstModeDo));

    final Mode mode1 = mock(Mode.class);
    when(mode1.getTopComponents()).thenReturn(new TopComponent[]{firstTcInFirstMode});


    final DataObject firstTcInSecondModeDo = mock(DataObject.class);
    final TopComponent firstTcInSecondMode = mock(TopComponent.class);
    when(firstTcInSecondMode.getLookup()).thenReturn(Lookups.fixed(firstTcInSecondModeDo));

    final DataObject secondTcInSecondModeDo = mock(DataObject.class);
    final TopComponent secondTcInSecondMode = mock(TopComponent.class);
    when(secondTcInSecondMode.getLookup()).thenReturn(Lookups.fixed(secondTcInSecondModeDo));

    final DataObject tc3InSecondModeDo = mock(DataObject.class);
    final TopComponent tc3InSecondMode = mock(TopComponent.class);
    when(tc3InSecondMode.getLookup()).thenReturn(Lookups.fixed(tc3InSecondModeDo));

    final Mode mode2 = mock(Mode.class);
    when(mode2.getTopComponents()).thenReturn(new TopComponent[]{firstTcInSecondMode, secondTcInSecondMode, tc3InSecondMode});


    final TopComponent.Registry registry = mock(TopComponent.Registry.class);
    when(registry.getActivated()).thenReturn(secondTcInSecondMode);

    final WindowManager windowManager = mock(WindowManager.class);
    when(windowManager.getRegistry()).thenReturn(registry);
    //noinspection rawtypes,unchecked
    when(windowManager.getModes()).thenReturn((Set) Set.of(mode1, mode2));

    try (MockedStatic<WindowManager> windowManagerMockedStatic = mockStatic(WindowManager.class))
    {
      windowManagerMockedStatic.when(WindowManager::getDefault).thenReturn(windowManager);

      final IDataObjectGroupProvider groupProvider = mock(IDataObjectGroupProvider.class);
      when(groupProvider.group(firstTcInFirstModeDo)).thenReturn(Optional.of("AAA"));
      when(groupProvider.group(firstTcInSecondModeDo)).thenReturn(Optional.of("AAA"));
      when(groupProvider.group(secondTcInSecondModeDo)).thenReturn(Optional.of("AAA"));
      when(groupProvider.group(tc3InSecondModeDo)).thenReturn(Optional.of("BBB"));

      try (MockedStatic<IDataObjectGroupProvider> groupProviderMockedStatic = mockStatic(IDataObjectGroupProvider.class))
      {
        groupProviderMockedStatic.when(IDataObjectGroupProvider::getDefault).thenReturn(groupProvider);

        final ActionEvent eventMock = mock(ActionEvent.class);
        final CloseGroupAction action = new CloseGroupAction();
        action.actionPerformed(eventMock);
        verifyNoInteractions(eventMock);

        verify(firstTcInFirstMode, never()).close();
        verify(firstTcInSecondMode).close();
        verify(secondTcInSecondMode).close();
        verify(tc3InSecondMode, never()).close();
      }
    }
  }
}
