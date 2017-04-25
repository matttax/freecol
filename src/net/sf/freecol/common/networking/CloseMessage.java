/**
 *  Copyright (C) 2002-2017   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.networking;

import javax.xml.stream.XMLStreamException;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.io.FreeColXMLReader;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;

import org.w3c.dom.Element;


/**
 * A message sent to clients to signal that a panel should be closed.
 */
public class CloseMessage extends AttributeMessage {

    public static final String TAG = "close";
    private static final String PANEL_TAG = "panel";


    /**
     * Create a new {@code CloseMessage}.
     */
    public CloseMessage(String panel) {
        super(TAG, PANEL_TAG, panel);
    }

    /**
     * Create a new {@code CloseMessage} from a supplied element.
     *
     * @param game The {@code Game} this message belongs to.
     * @param element The {@code Element} to use to create the message.
     */
    public CloseMessage(Game game, Element element) {
        super(TAG, PANEL_TAG, getStringAttribute(element, PANEL_TAG));
    }

    /**
     * Create a new {@code CloseMessage} from a stream.
     *
     * @param game The {@code Game} this message belongs to.
     * @param xr The {@code FreeColXMLReader} to read from.
     * @exception XMLStreamException if the stream is corrupt.
     * @exception FreeColException if the internal message can not be read.
     */
    public CloseMessage(Game game, FreeColXMLReader xr)
        throws FreeColException, XMLStreamException {
        super(TAG, xr, PANEL_TAG);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public MessagePriority getPriority() {
        return Message.MessagePriority.LAST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aiHandler(FreeColServer freeColServer, AIPlayer aiPlayer) {
        // Ignored
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clientHandler(FreeColClient freeColClient) {
        // This is a trivial handler to allow the server to signal to
        // the client that an offer that caused a popup (for example,
        // a native demand or diplomacy proposal) has not been
        // answered quickly enough and that the offering player has
        // assumed this player has refused-by-inaction, and therefore,
        // the popup needs to be closed.
        igc(freeColClient).closeHandler(getPanel());
    }


    // Public interface

    public String getPanel() {
        return getStringAttribute(PANEL_TAG);
    }
}