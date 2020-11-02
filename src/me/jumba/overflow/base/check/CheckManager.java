package me.jumba.overflow.base.check;

import lombok.Getter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.checks.combat.aim.*;
import me.jumba.overflow.checks.combat.aimassist.AimAssistA;
import me.jumba.overflow.checks.combat.aimassist.AimAssistB;
import me.jumba.overflow.checks.combat.autoclicker.AutoClickerA;
import me.jumba.overflow.checks.combat.autoclicker.AutoClickerB;
import me.jumba.overflow.checks.combat.bot.Entity;
import me.jumba.overflow.checks.combat.bot.Raycast;
import me.jumba.overflow.checks.combat.hitbox.HitBoxA;
import me.jumba.overflow.checks.combat.killaura.*;
import me.jumba.overflow.checks.combat.reach.ReachA;
import me.jumba.overflow.checks.combat.velocity.Velocity;
import me.jumba.overflow.checks.movement.flight.FlightA;
import me.jumba.overflow.checks.movement.flight.FlightB;
import me.jumba.overflow.checks.movement.invalid.*;
import me.jumba.overflow.checks.movement.motion.MotionA;
import me.jumba.overflow.checks.movement.speed.SpeedA;
import me.jumba.overflow.checks.movement.speed.SpeedB;
import me.jumba.overflow.checks.movement.speed.SpeedC;
import me.jumba.overflow.checks.movement.speed.SpeedD;
import me.jumba.overflow.checks.other.badpackets.*;
import me.jumba.overflow.checks.other.inventory.InventoyA;
import me.jumba.overflow.checks.other.pingspoof.PingSpoofA;
import me.jumba.overflow.checks.other.scaffold.ScaffoldA;
import me.jumba.overflow.checks.other.timer.TimerA;
import me.jumba.overflow.checks.other.timer.TimerB;
import me.jumba.overflow.util.file.ChecksFile;
import me.jumba.overflow.util.math.Verbose;
import me.jumba.overflow.util.version.VersionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.base.check
 */
@Getter
public class CheckManager {

    private List<Check> checkList = new ArrayList<>(), startUpChecks = new ArrayList<>();

    private int count = 1;

    public static long lastCheck;

    private boolean flag;

    private boolean c;

    public CheckManager() {


            addCheck(new AimAssistA("AimAssist", "A", CheckType.COMBAT, true));
            addCheck(new AimAssistB("AimAssist", "B", CheckType.COMBAT, true));

            addCheck(new ReachA("Reach", "A", CheckType.COMBAT, true));

            addCheck(new AutoClickerA("Autoclicker", "A", CheckType.COMBAT, true));
            addCheck(new AutoClickerB("Autoclicker", "B", CheckType.COMBAT, true));

            addCheck(new KillauraA("Killaura", "A", CheckType.COMBAT, true));
            addCheck(new KIllauraB("Killaura", "B", CheckType.COMBAT, true));
            addCheck(new KIllauraC("Killaura", "C", CheckType.COMBAT, true));
            addCheck(new KIllauraD("Killaura", "D", CheckType.COMBAT, true));
            addCheck(new KillauraE("Killaura", "E", CheckType.COMBAT, true));
            addCheck(new KillauraF("Killaura", "F", CheckType.COMBAT, true));
            addCheck(new KillauraG("Killaura", "G", CheckType.COMBAT, true));
            addCheck(new KillauraH("Killaura", "H", CheckType.COMBAT, true));
            addCheck(new KillauraI("Killaura", "I", CheckType.COMBAT, true));

            addCheck(new AimA("Aim", "A", CheckType.COMBAT, true));
            addCheck(new AimB("Aim", "B", CheckType.COMBAT, true));
            addCheck(new AimC("Aim", "C", CheckType.COMBAT, true));
            addCheck(new AimD("Aim", "D", CheckType.COMBAT, true));
            addCheck(new AimE("Aim", "E", CheckType.COMBAT, true));
            addCheck(new AimF("Aim", "F", CheckType.COMBAT, true));
            addCheck(new AimG("Aim", "G", CheckType.COMBAT, true));
            addCheck(new AimH("Aim", "H", CheckType.COMBAT, true));
            addCheck(new AimJ("Aim", "J", CheckType.COMBAT, true));
            addCheck(new AimK("Aim", "K", CheckType.COMBAT, true));
            addCheck(new AimL("Aim", "L", CheckType.COMBAT, true));

            addCheck(new HitBoxA("HitBox", "A", CheckType.COMBAT, true));

            addCheck(new FlightA("Flight", "A", CheckType.MOVEMENT, true));
            addCheck(new FlightB("Flight", "B", CheckType.MOVEMENT, true));

            addCheck(new SpeedA("Speed", "A", CheckType.MOVEMENT, true));
            addCheck(new SpeedB("Speed", "B", CheckType.MOVEMENT, true));
            addCheck(new SpeedC("Speed", "C", CheckType.MOVEMENT, true));
            addCheck(new SpeedD("Speed", "D", CheckType.MOVEMENT, true));

            addCheck(new InvalidA("Invalid", "A", CheckType.MOVEMENT, true));
            addCheck(new InvalidB("Invalid", "B", CheckType.MOVEMENT, true));
            addCheck(new InvalidC("Invalid", "C", CheckType.MOVEMENT, true));
            addCheck(new InvalidD("Invalid", "D", CheckType.MOVEMENT, true));
            addCheck(new InvalidE("Invalid", "E", CheckType.MOVEMENT, true));
            addCheck(new InvalidI("Invalid", "I", CheckType.MOVEMENT, true));
            addCheck(new InvalidJ("Invalid", "J", CheckType.MOVEMENT, true));
            addCheck(new InvalidK("Invalid", "k", CheckType.MOVEMENT, true));
            addCheck(new InvalidL("Invalid", "L", CheckType.MOVEMENT, true));
            addCheck(new InvalidM("Invalid", "M", CheckType.MOVEMENT, true));
            addCheck(new InvalidO("Invalid", "O", CheckType.MOVEMENT, true));
            addCheck(new InvalidP("Invalid", "P", CheckType.MOVEMENT, true));
            addCheck(new InvalidQ("Invalid", "Q", CheckType.MOVEMENT, true));
            addCheck(new InvalidR("Invalid", "R", CheckType.MOVEMENT, true));

            addCheck(new BadPacketsA("BadPackets", "A", CheckType.OTHER, true));
            addCheck(new BadPacketsB("BadPackets", "B", CheckType.OTHER, true));
            addCheck(new BadPacketsC("BadPackets", "C", CheckType.OTHER, true));
            addCheck(new BadPacketsD("BadPackets", "D", CheckType.OTHER, true));
            addCheck(new BadPacketsE("BadPackets", "E", CheckType.OTHER, true));
            addCheck(new BadPacketsF("BadPackets", "F", CheckType.OTHER, true));
            addCheck(new BadPacketsG("BadPackets", "G", CheckType.OTHER, true));
            addCheck(new BadPacketsH("BadPackets", "I", CheckType.OTHER, true));

            addCheck(new ScaffoldA("Scaffold", "A", CheckType.OTHER, true));

            addCheck(new TimerA("Timer", "A", CheckType.OTHER, true));
            addCheck(new TimerB("Timer", "B", CheckType.OTHER, true));

            addCheck(new PingSpoofA("PingSpoof", "A", CheckType.OTHER, true));

            addCheck(new InventoyA("Inventory", "A", CheckType.OTHER, true));

            addCheck(new MotionA("Motion", "A", CheckType.OTHER, true));

            addCheck(new Velocity("Velocity", "A", CheckType.OTHER, true));

            if (Overflow.getInstance().getVersionUtil().getVersion() == VersionUtil.Version.V1_8) {
                addCheck(new Entity("Entity", "A", CheckType.COMBAT, true));
                addCheck(new Raycast("Raycast", "A", CheckType.COMBAT, true));
            }

            setup();
        }
    


    public boolean isEnabled(String name) {
        for (Check check : getCheckList()) {
            if (check.getCheckName().equalsIgnoreCase(name) && check.getType().equalsIgnoreCase("A")) {
                return check.isEnabled();
            }
        }
        return false;
    }


    public void unRegisterAll() {
        checkList.parallelStream().forEach(check -> Overflow.getInstance().getEventManager().unregisterListener(check));
    }

    public boolean isEnabled(String name, String type) {
        for (Check check : getCheckList()) {
            if (check.getCheckName().equalsIgnoreCase(name) && check.getType().equalsIgnoreCase(type)) {
                return check.isEnabled();
            }
        }
        return false;
    }

    private void setup() {
        ChecksFile.getInstance().setup(Overflow.getLauncherInstance());

        checkList.forEach(check -> {
            check.setAstorment(count);
            count++;

            String enabledPath = String.format(Overflow.getInstance().getClassResolver().getData(Overflow.getInstance().getClassResolver().IDENT2), check.getCheckName() + check.getType());
            String autobansPath = String.format(Overflow.getInstance().getClassResolver().getData(Overflow.getInstance().getClassResolver().IDENT3), check.getCheckName() + check.getType());

            if (!ChecksFile.getInstance().getData().contains(enabledPath)) {
                ChecksFile.getInstance().getData().set(enabledPath, check.isEnabled());
            }

            if (ChecksFile.getInstance().getData().contains(enabledPath)) {
                check.setCheckEnabled(ChecksFile.getInstance().getData().getBoolean(enabledPath));
            }

            if (!ChecksFile.getInstance().getData().contains(autobansPath)) {
                ChecksFile.getInstance().getData().set(autobansPath, check.isAutobans());
            }

            if (ChecksFile.getInstance().getData().contains(autobansPath)) {
                check.setAutobans(ChecksFile.getInstance().getData().getBoolean(autobansPath));
            }
        });
        ChecksFile.getInstance().saveData();
    }

    public void saveChecks() {
        Overflow.getInstance().getExecutorService().execute(() -> {
            ChecksFile.getInstance().setup(Overflow.getLauncherInstance());

            checkList.forEach(check -> {
                String enabledPath = String.format(Overflow.getInstance().getClassResolver().getData(Overflow.getInstance().getClassResolver().IDENT2), check.getCheckName() + check.getType());
                String autobansPath = String.format(Overflow.getInstance().getClassResolver().getData(Overflow.getInstance().getClassResolver().IDENT3), check.getCheckName() + check.getType());
                ChecksFile.getInstance().getData().set(enabledPath, check.isEnabled());
                ChecksFile.getInstance().getData().set(autobansPath, check.isAutobans());
            });

            ChecksFile.getInstance().saveData();
        });
    }

    private void addCheck(Check check) {
        if (!checkList.contains(check)) {
            checkList.add(check);
        }
    }
}
