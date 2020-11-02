package me.jumba.overflow.listener;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.event.SparkyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.listener
 */
public class ListenerManager {
    private List<SparkyListener> listenerList = new ArrayList<>();

    public ListenerManager() {
        addListener(new PacketListener());

        setup();
    }

    private void setup() {
        listenerList.forEach(sparkyListener -> Overflow.getInstance().getEventManager().registerListeners(sparkyListener, Overflow.getLauncherInstance()));
    }

    private void addListener(SparkyListener sparkyListener) {
        if (!listenerList.contains(sparkyListener)) listenerList.add(sparkyListener);
    }
}
