package me.jumba.overflow;

import lombok.Getter;
import lombok.Setter;
import me.jumba.auth.AuthUtils;
import me.jumba.auth.resolver.ClassResolver;
import me.jumba.auth.util.StringUtil;
import me.jumba.overflow.base.check.CheckManager;
import me.jumba.overflow.base.command.CommandManager;
import me.jumba.overflow.base.event.EventManager;
import me.jumba.overflow.base.event.bukkit.SparkySetupEvent;
import me.jumba.overflow.base.event.impl.ServerShutdownEvent;
import me.jumba.overflow.base.tinyprotocol.api.TinyProtocolHandler;
import me.jumba.overflow.base.tinyprotocol.api.packets.reflections.Reflections;
import me.jumba.overflow.base.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.jumba.overflow.base.user.UserManager;
import me.jumba.overflow.config.ConfigLoader;
import me.jumba.overflow.config.ConfigManager;
import me.jumba.overflow.hook.HookManager;
import me.jumba.overflow.util.box.BlockBoxManager;
import me.jumba.overflow.util.box.impl.BoundingBoxes;
import me.jumba.overflow.util.command.CommandUtils;
import me.jumba.overflow.util.file.KeyFile;
import me.jumba.overflow.util.http.HTTPUtil;
import me.jumba.overflow.util.processor.EntityProcessor;
import me.jumba.overflow.util.processor.LogsProcessor;
import me.jumba.overflow.util.string.StringUtils;
import me.jumba.overflow.util.version.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created on 05/01/2020 Package me.jumba.sparky
 */

@Getter
@Setter
public class Overflow {

    @Getter
    private static Overflow instance;

    @Getter
    private static Plugin launcherInstance;

    private String version = "3.13", key;

    private ScheduledExecutorService executorService, processorService, checkExecutor, lagProcessorExecutor, logsExecutor;

    private TinyProtocolHandler tinyProtocolHandler;

    private String bukkitVersion;

    private UserManager userManager;

    private EventManager eventManager;

    private VersionUtil versionUtil;

    private BlockBoxManager blockBoxManager;

    private BoundingBoxes boxes;

    private CommandManager commandManager;

    private CheckManager checkManager;

    private Map<UUID, List<Entity>> entities = new ConcurrentHashMap<>();

    private WrappedField entityList = null;

    private HookManager hookManager;

    private long lastServerTick, lastServerLag, lastServerStart;

    private boolean isLagging, setup, active = true;

    private ConfigManager configManager = new ConfigManager();

    private ConfigLoader configLoader = new ConfigLoader();

    private int currentTicks;

    private List<Player> banwaveList = new CopyOnWriteArrayList<>();

    private ClassResolver classResolver;

    private EntityProcessor entityProcessor = new EntityProcessor();

    private int lagStartCheck;

    private AuthUtils authUtils = new AuthUtils();

    private HashMap<UUID, List<String>> sessionLogs = new HashMap<>(), totalLogs = new HashMap<>();

    private LogsProcessor logsProcessor = new LogsProcessor();

    public void start(Plugin plugin) {
       // launcherInstance = plugin;

        instance = this;
                configLoader.load();

                bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);

                tinyProtocolHandler = new TinyProtocolHandler();

                processorService = Executors.newSingleThreadScheduledExecutor();
                checkExecutor = Executors.newSingleThreadScheduledExecutor();
                executorService = Executors.newSingleThreadScheduledExecutor();
                lagProcessorExecutor = Executors.newSingleThreadScheduledExecutor();
                logsExecutor = Executors.newSingleThreadScheduledExecutor();

                this.blockBoxManager = new BlockBoxManager();
                this.boxes = new BoundingBoxes();

                userManager = new UserManager();
                eventManager = new EventManager();

                versionUtil = new VersionUtil();

                commandManager = new CommandManager();
                new CheckManager();



                hookManager = new HookManager();

                lastServerStart = System.currentTimeMillis();

                startLagCheck();

                Bukkit.getServer().getPluginManager().callEvent(new SparkySetupEvent());

                Bukkit.getServer().getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().addChannel(player));

                entityProcessor.start();

                logsProcessor.start();

                this.setup = true;
            }

    
    private void startLagCheck() {
        executorService.scheduleAtFixedRate(() -> {

            if (lagStartCheck > 70) {
                long time = (System.currentTimeMillis() - lastServerTick);

                if (time > (getConfigManager().getMaxLagTime() + 100L)) {
                    lastServerLag = System.currentTimeMillis();
                }

                isLagging = (System.currentTimeMillis() - lastServerLag) < (1000L * 5L);
            }

            if (lagStartCheck < 100) lagStartCheck++;
        }, 1L, 1L, TimeUnit.MILLISECONDS);

        new BukkitRunnable() {
            @Override
            public void run() {
                lastServerTick = System.currentTimeMillis();
            }
        }.runTaskTimer(getLauncherInstance(), 1L, 1L);

        executorService.scheduleAtFixedRate(() -> currentTicks++, 50L, 50L, TimeUnit.MILLISECONDS);
    }


    public void shutdown() {
        getEventManager().callEvent(new ServerShutdownEvent());

        getUserManager().getUsers().forEach(user -> user.getExecutorService().shutdownNow());

        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().removeChannel(player));
        executorService.shutdownNow();
        checkExecutor.shutdownNow();
        processorService.shutdownNow();
        lagProcessorExecutor.shutdownNow();

        commandManager.getCommandList().forEach(CommandUtils::unRegisterBukkitCommand);
        CommandUtils.unRegisterBukkitCommand("overflow");
        CommandUtils.unRegisterBukkitCommand(getConfigManager().getCustomCommand());
    }

}
