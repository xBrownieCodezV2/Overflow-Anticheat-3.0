package me.jumba.overflow.util.processor;

import lombok.Getter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.util.time.RunUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created on 27/05/2020 Package me.jumba.sparky.util.processor
 */

@Getter
public class EntityProcessor {

    private Map<UUID, List<Entity>> vehicles = new ConcurrentHashMap<>();
    private BukkitTask task;

    private void runEntityProcessor() {
        Overflow.getInstance().getEntities().keySet().forEach((uuid) -> vehicles.put(uuid, Overflow.getInstance().getEntities().get(uuid).stream().filter(entity -> entity instanceof Vehicle).collect(Collectors.toList())));
    }

    public void start() {
        task = RunUtils.taskTimerAsync(this::runEntityProcessor, Overflow.getLauncherInstance(), 0L, 10L);
    }
}
