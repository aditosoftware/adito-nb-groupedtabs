package de.adito.aditoweb.nbm.groupedtabs.actions;

import de.adito.aditoweb.nbm.groupedtabs.*;
import de.adito.nbm.groupedtabs.api.IDataObjectGroupProvider;
import org.jetbrains.annotations.*;
import org.openide.awt.*;
import org.openide.loaders.DataObject;
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.windows.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.stream.*;

/**
 * Action Menu Popup that enables the user to choose a group explicitly.
 *
 * @author p.neub, 27.02.2023
 */
@ActionID(category = "Window/SelectDocumentNode", id = "de.adito.aditoweb.nbm.groupedtabs.actions.SelectGroupAction")
@ActionRegistration(displayName = "#LBL_SelectGroup_Action")
@ActionReference(path = "Editors/TabActions", position = -10010)
public final class SelectGroupAction extends AbstractAction implements Presenter.Popup
{
  @Override
  public void actionPerformed(ActionEvent e)
  {
    // actionPerformed cannot be called on a PopUp action
    throw new UnsupportedOperationException("invalid call to SelectGroupAction.actionPerformed inside a PopUp action");
  }

  @Override
  public JMenuItem getPopupPresenter()
  {
    final JMenuItem menu = new GroupingMenu();
    menu.setText(NbBundle.getMessage(SelectGroupAction.class, "LBL_SelectGroup_Action"));
    return menu;
  }

  /**
   * Menu that holds the possible groups as {@link JRadioButtonMenuItem}s.
   */
  static final class GroupingMenu extends JMenu
  {
    /**
     * Constructor of the Menu.
     * This fills the Menu with the currently used groups.
     */
    public GroupingMenu()
    {
      // Get the current TopComponent, note that this is the currently active TopComponent,
      // not nessersarry the TopComponent that the context menu was opened on.
      // Currently, there is way to do this. The same problem also applies to the NetBeans Editors Action
      // see org.netbeans.core.multiview.EditorsAction.
      final TopComponent tc = NbUtils.getActiveTopComponent();

      // Get the current group if it was explicitly set for the current TopComponent
      final String currentGroup = (String) tc.getClientProperty(Group.PROP_GROUP);

      // add default menu item, that resets the explicit group
      final JMenuItem defaultItem = new JRadioButtonMenuItem(NbBundle.getMessage(SelectGroupAction.class, "LBL_SelectGroup_Action_DefaultItem"));
      defaultItem.setSelected(currentGroup == null);
      defaultItem.addActionListener(e -> updateTopComponentGroup(tc, null));
      add(defaultItem);
      addSeparator();

      // add menu items corrosponding to each cuurrently used group
      getGroupsInTopComponentMode(tc).forEachOrdered(pGroup -> {
        final JMenuItem item = new ColouredJRadioButtonMenuItem(pGroup, Group.colorForGroup(pGroup));
        item.setSelected(pGroup.equals(currentGroup));
        item.addActionListener(e -> updateTopComponentGroup(tc, pGroup));
        add(item);
      });
    }

    @NotNull
    static Stream<String> getGroupsInTopComponentMode(@NotNull TopComponent pTopComponent)
    {
      final IDataObjectGroupProvider groupProvider = IDataObjectGroupProvider.getDefault();

      return NbUtils.getTopComponentsInMode(pTopComponent)
          .filter(TopComponent::isOpened)

          // get the underlaying DataObject for the TopComponent and fetch the group using it
          .map(pTc -> pTc.getLookup().lookup(DataObject.class))
          .filter(Objects::nonNull)
          .map(groupProvider::group)

          // flatmap Optional
          .flatMap(Optional::stream)

          // only show each group once but sorted
          .distinct()
          .sorted();
    }

    /**
     * Helper to set the color of a {@link TopComponent} while also rerendering it.
     *
     * @param pTopComponent the target {@link TopComponent}
     * @param pGroup        the group that should be explicitly set for the {@link TopComponent}
     */
    private static void updateTopComponentGroup(@NotNull TopComponent pTopComponent, @Nullable String pGroup)
    {
      pTopComponent.putClientProperty(Group.PROP_GROUP, pGroup);
      WindowManager.getDefault().getMainWindow().repaint();
    }
  }

  /**
   * {@link JRadioButtonMenuItem} that also has a color indicator.
   */
  private static final class ColouredJRadioButtonMenuItem extends JRadioButtonMenuItem
  {
    private static final int COLOR_RECT_WIDTH = 10;

    private final Color color;

    /**
     * Constructor of {@link ColouredJRadioButtonMenuItem}
     *
     * @param pText  the text of the menu item
     * @param pColor the color indicator of the menu item
     */
    public ColouredJRadioButtonMenuItem(@NotNull String pText, @NotNull Color pColor)
    {
      super(pText);
      setBorder(BorderFactory.createEmptyBorder(0, 0, 0, COLOR_RECT_WIDTH));
      color = pColor;
    }

    @Override
    public void paint(Graphics g)
    {
      super.paint(g);
      g.setColor(color);
      g.fillRect(getWidth() - COLOR_RECT_WIDTH, 0, getWidth(), getHeight());
    }
  }
}
