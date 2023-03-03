package de.adito.aditoweb.nbm.groupedtabs.impl;

import de.adito.aditoweb.nbm.groupedtabs.*;
import de.adito.nbm.groupedtabs.api.IDataObjectGroupProvider;
import org.netbeans.core.multitabs.TabDecorator;
import org.netbeans.swing.tabcontrol.TabData;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.util.*;

/**
 * Custom {@link TabDecorator} that paints the group color above the tab.
 *
 * @author p.neub, 27.02.2023
 */
@ServiceProvider(service = TabDecorator.class)
public final class GroupingTabDecorator extends TabDecorator
{
  @Override
  public void paintAfter(TabData tab, Graphics g, Rectangle tabRect, boolean isSelected)
  {
    final Color color = Optional.ofNullable(tab)
        // extract TopComponent from TabData
        .map(TabData::getComponent)
        .filter(TopComponent.class::isInstance)
        .map(TopComponent.class::cast)

        // check for IGroup.PROP_GROUP, otherwise return the grup using IDataObjectGroupProvider
        .flatMap(tc -> Optional.ofNullable((String) tc.getClientProperty(IGroup.PROP_GROUP))
            .or(() -> Optional.ofNullable(tc.getLookup().lookup(DataObject.class))
                .flatMap(pDataObject -> IDataObjectGroupProvider.getDefault().group(pDataObject))))

        // get color for group and fallback to IGroup.FALLBACK, if the group could not be determined
        .map(IGroup::colorForGroup)
        .orElse(IGroup.FALLBACK);

    // draw color above the Tab
    g.setColor(color);
    g.fillRect(tabRect.x, tabRect.y, tabRect.width, tabRect.height / 10);
  }
}
