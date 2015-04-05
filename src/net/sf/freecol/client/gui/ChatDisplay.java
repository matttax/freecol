/**
 *  Copyright (C) 2002-2015   The FreeCol Team
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

package net.sf.freecol.client.gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Date;
import java.util.ArrayList;

/**
 * ChatDisplay manages use of <code>GUIMessage</code>.
 */
public class ChatDisplay {

    /** The number of messages getting remembered. */
    private static final int MESSAGE_COUNT = 3;

    /** The amount of time before a message gets deleted (in milliseconds). */
    private static final int MESSAGE_AGE = 30000;

    private final ArrayList<GUIMessage> messages;

    ChatDisplay() {
        messages = new ArrayList<>(MESSAGE_COUNT);
    }

    /**
     * Adds a message to the list of messages that need to be displayed
     * on the GUI.
     *
     * @param message The message to add.
     */
    public synchronized void addMessage(GUIMessage message) {
        if (getMessageCount() == MESSAGE_COUNT) {
            messages.remove(0);
        }
        messages.add(message);
    }

    /**
     * Gets the message at position 'index'. The message at position 0
     * is the oldest message and is most likely to be removed during
     * the next call of removeOldMessages().  The higher the index of
     * a message, the more recently it was added.
     *
     * @param index The index of the message to return.
     * @return The message at position 'index'.
     */
    private GUIMessage getMessage(int index) {
        return messages.get(index);
    }

    /**
     * Gets the amount of message that are currently being displayed
     * on this GUI.
     *
     * @return The amount of message that are currently being
     *     displayed on this GUI.
     */
    private int getMessageCount() {
        return messages.size();
    }

    /**
     * Displays the list of messages.
     * 
     * @param g The Graphics2D the messages should be displayed on.
     * @param fontLibrary The FontLibrary to use.
     * @param height The height of the space for displaying in.
     */
    public synchronized void display(Graphics2D g, FontLibrary fontLibrary, int height) {
        if (getMessageCount() > 0) {
            // Don't edit the list of messages while I'm drawing them.
            Font font = fontLibrary.createScaledFont(
                FontLibrary.FontType.NORMAL, FontLibrary.FontSize.TINY);
            GUIMessage message = getMessage(0);
            Image si = ImageLibrary.getStringImage(
                g, message.getMessage(), message.getColor(), font);
            int yy = height - 300 - getMessageCount() * si.getHeight(null);
            int xx = 40;

            for (int i = 0; i < getMessageCount(); i++) {
                message = getMessage(i);
                g.drawImage(ImageLibrary.getStringImage(g,
                        message.getMessage(), message.getColor(), font),
                    xx, yy, null);
                yy += si.getHeight(null);
            }
        }
    }

    /**
     * Removes all the message that are older than MESSAGE_AGE.
     *
     * This can be useful to see if it is necessary to refresh the
     * screen.
     *
     * @return True if at least one message has been removed,
     */
    public synchronized boolean removeOldMessages() {
        long currentTime = new Date().getTime();
        boolean result = false;

        int i = 0;
        while (i < getMessageCount()) {
            long creationTime = getMessage(i).getCreationTime();
            if ((currentTime - creationTime) >= MESSAGE_AGE) {
                result = true;
                messages.remove(i);
            } else {
                i++;
            }
        }
        return result;
    }

}
