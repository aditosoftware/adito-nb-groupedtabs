package de.adito.aditoweb.nbm.groupedtabs.actions;

import de.adito.nbm.groupedtabs.api.IDataObjectGroupProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.openide.filesystems.*;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests methods inside the {@link SortTabsAction} class
 *
 * @author p.neub, 02.03.2023
 */
class SortTabsActionTest
{
  private DataObject myEntity;
  private DataObject myEntityProcess;
  private DataObject myView;
  private DataObject myProcess;

  /**
   * Creats the following in memory file tree for testing:
   * <pre>
   * /
   * - AditoProjects/
   * - - MyProject/
   * - - - entities/
   * - - - - MyEntity/
   * - - - - - MyEntity.aod
   * - - - - - - nested1/
   * - - - - - - - nested2/
   * - - - - - - - - process.js
   * - - - neonViews/
   * - - - - MyView/
   * - - - - - MyView.aod
   * - - - process/
   * - - - - MyProcess/
   * - - - - - process.js
   * </pre>
   */
  @BeforeEach
  @SneakyThrows
  void setUp()
  {
    final FileSystem fs = FileUtil.createMemoryFileSystem();
    final FileObject root = fs.getRoot();

    final FileObject projects = root.createFolder("AditoProjects");
    final FileObject project = projects.createFolder("MyProject");

    final FileObject entities = project.createFolder("entities");
    final FileObject neonViews = project.createFolder("neonViews");
    final FileObject process = project.createFolder("process");

    final FileObject myEntityDir = entities.createFolder("MyEntity");
    myEntity = DataObject.find(myEntityDir.createData("MyEntity.aod"));

    final FileObject myEntityDirNested1 = myEntityDir.createFolder("nested1");
    final FileObject myEntityDirNested2 = myEntityDirNested1.createFolder("nested2");
    myEntityProcess = DataObject.find(myEntityDirNested2.createData("process.js"));

    final FileObject myViewDir = neonViews.createFolder("MyView");
    myView = DataObject.find(myViewDir.createData("MyView.aod"));

    final FileObject myProcessDir = process.createFolder("MyProcess");
    myProcess = DataObject.find(myProcessDir.createData("process.js"));
  }

  /**
   * Tests that TopComponents are sorted correctly.
   * The sorting is performed using {@link SortTabsAction#getSortedOpenedTopComponents(TopComponent[])}.
   */
  @Test
  void shouldSortTopComponentsCorrectly()
  {
    final IDataObjectGroupProvider groupProvider = mock(IDataObjectGroupProvider.class);

    // alphabetical ordering with nulls last would be Some(A_MyView) -> Some(Z_MyEntity) -> None
    when(groupProvider.group(myEntity)).thenReturn(Optional.of("Z_MyEntity"));
    when(groupProvider.group(myEntityProcess)).thenReturn(Optional.of("Z_MyEntity"));
    when(groupProvider.group(myView)).thenReturn(Optional.of("A_MyView"));

    // ordering is: entity -> process
    // the other groups don't need mocked ordering since there is only one item in the group
    when(groupProvider.compare(myEntity, myEntityProcess)).thenReturn(-1);
    when(groupProvider.compare(myEntityProcess, myEntity)).thenReturn(1);

    try (final MockedStatic<IDataObjectGroupProvider> mockedStaticGroupProvider = mockStatic(IDataObjectGroupProvider.class))
    {
      mockedStaticGroupProvider.when(IDataObjectGroupProvider::getDefault).thenReturn(groupProvider);

      final TopComponent myEntityTc = mock(TopComponent.class);
      when(myEntityTc.isOpened()).thenReturn(true);
      when(myEntityTc.getLookup()).thenReturn(Lookups.fixed(myEntity));

      final TopComponent myEntityProcessTc = mock(TopComponent.class);
      when(myEntityProcessTc.isOpened()).thenReturn(true);
      when(myEntityProcessTc.getLookup()).thenReturn(Lookups.fixed(myEntityProcess));

      final TopComponent myViewTc = mock(TopComponent.class);
      when(myViewTc.isOpened()).thenReturn(true);
      when(myViewTc.getLookup()).thenReturn(Lookups.fixed(myView));

      final TopComponent myProcessTc = mock(TopComponent.class);
      when(myProcessTc.isOpened()).thenReturn(true);
      when(myProcessTc.getLookup()).thenReturn(Lookups.fixed(myProcess));

      final TopComponent hiddenTc = mock(TopComponent.class);
      when(hiddenTc.isOpened()).thenReturn(false);

      final TopComponent[] unsorted = new TopComponent[]{myViewTc, myEntityTc, myProcessTc, hiddenTc, myEntityProcessTc};
      final List<TopComponent> sorted = SortTabsAction.getSortedOpenedTopComponents(unsorted);

      final List<TopComponent> expected = List.of(myViewTc, myEntityTc, myEntityProcessTc, myProcessTc);
      assertEquals(expected, sorted);
    }
  }
}
