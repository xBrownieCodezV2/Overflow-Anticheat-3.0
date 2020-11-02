package me.jumba.overflow.util.processor;

import lombok.Setter;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInClientCommand;
import me.jumba.overflow.base.user.User;

/**
 * Created on 19/03/2020 Package me.jumba.sparky.util.processor.lag
 */
@Setter
public class OtherProcessor {
    private User user;

    public void update(Object packet, String type) {
        if (user != null) {

            if (type.equalsIgnoreCase(Packet.Client.CLIENT_COMMAND)) {
                WrappedInClientCommand wrappedInClientCommand = new WrappedInClientCommand(packet, user.getPlayer());

                if (wrappedInClientCommand.getCommand() == WrappedInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    user.getMiscData().setInventoryOpen(true);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.CLOSE_WINDOW)) {
                user.getMiscData().setInventoryOpen(false);
            }
        }
    }
}
