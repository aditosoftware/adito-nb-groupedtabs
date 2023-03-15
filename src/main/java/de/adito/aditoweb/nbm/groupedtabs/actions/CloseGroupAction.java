package de.adito.aditoweb.nbm.groupedtabs.actions;

import de.adito.aditoweb.nbm.groupedtabs.NbUtils;
import de.adito.aditoweb.nbm.groupedtabs.api.IDataObjectGroupProvider;
import org.openide.awt.*;
import org.openide.loaders.DataObject;
import org.openide.util.*;
import org.openide.windows.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.Optional;

/**
 * Action that closes all TopComponents in the Mode of the active TopComponent that are in the same group
 *
 * @author p.neub, 13.03.2023
 */
@ActionID(category = "Window/SelectDocumentNode", id = "de.adito.aditoweb.nbm.groupedtabs.actions.CloseGroupAction")
@ActionRegistration(displayName = "#LBL_CloseGroup_Action")
@ActionReferences({
    @ActionReference(path = "Editors/TabActions", position = -10005),
    @ActionReference(path = "Shortcuts", name = "DA-W")
})
public class CloseGroupAction extends AbstractAction
{
  @Override
  public void actionPerformed(ActionEvent e)
  {
    final TopComponent tc = NbUtils.getActiveTopComponent();
    final IDataObjectGroupProvider groupProvider = IDataObjectGroupProvider.getDefault();
    Optional.of(tc.getLookup().lookup(DataObject.class))
        .map(groupProvider::group)
        .ifPresent(pGroup -> NbUtils.getTopComponentsInMode(tc)
            .flatMap(NbUtils::resolveDataObjects)
            .filter(pTc -> pGroup.equals(groupProvider.group(pTc.second())))
            .map(Pair::first)
            .forEach(TopComponent::close));
  }
}
