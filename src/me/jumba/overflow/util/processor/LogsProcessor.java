package me.jumba.overflow.util.processor;

import lombok.Getter;
import me.jumba.auth.util.http.HTTPUtil;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created on 07/08/2020 Package me.jumba.overflow.util.processor
 */

@Getter
public class LogsProcessor {

    private String time;

    private ConcurrentHashMap<String, List<String>> queue = new ConcurrentHashMap<>();

    public void start() {
        Overflow.getInstance().getLogsExecutor().scheduleAtFixedRate(() -> {
            Overflow.getInstance().getTotalLogs().forEach(((uuid, dataFromList) -> queue.put(uuid.toString(), dataFromList)));

            Overflow.getInstance().getTotalLogs().clear();
        }, 10L, 10, TimeUnit.SECONDS);

        Overflow.getInstance().getLogsExecutor().scheduleAtFixedRate(() -> {
            time = TimeUtils.getSystemTime();

            try {
                final boolean[] found = {false};

                queue.forEach(((s, list) -> {

                    if (!found[0]) {
                        addLog(s, list);
                        found[0] = true;
                        queue.remove(s);
                    }
                }));

            } catch (Exception ignored) {
            }

        }, 1L, 1L, TimeUnit.SECONDS);
    }

    public void removeLogs(String UUID) {
        HashMap<String, String> headers = new HashMap<>();

        headers.put("KEY", Overflow.getInstance().getAuthUtils().getKey());
        headers.put("XOR", "NOCleNtaBoxIachaRySTUCIAGHTMonJauNgHtesTitIgHTYCLerEGRATIceRYLAtLACinCHrANDREmAIngSteRonisPOLECtATe");
        headers.put("UUID", UUID);
        headers.put("MODE", "DELETE");

        HTTPUtil.getResponse("http://v2panel.overflowac.pw/addPlayerLog.php", headers);

        headers.clear();
    }

    public List<String> getLogs(String UUID) {

        HashMap<String, String> headers = new HashMap<>();

        headers.put("KEY", Overflow.getInstance().getAuthUtils().getKey());
        headers.put("XOR", "NOCleNtaBoxIachaRySTUCIAGHTMonJauNgHtesTitIgHTYCLerEGRATIceRYLAtLACinCHrANDREmAIngSteRonisPOLECtATe");
        headers.put("UUID", UUID);
        headers.put("MODE", "VIEW");

        String fromServer = HTTPUtil.getResponse("http://v2panel.overflowac.pw/addPlayerLog.php", headers);

        if (fromServer.length() > 1 && !fromServer.contains("[ERROR]")) {

            String decoded = new String(Base64.getDecoder().decode(fromServer));

            List<String> data = new ArrayList<>(Arrays.asList(decoded.split("<LINE>")));

            headers.clear();

            return data;
        } else {
            return new ArrayList<>();
        }
    }

    private void addLog(String UUID, List<String> logs) {

        if (logs.size() > 0) {
            new Thread(() -> {
                StringBuilder stringBuilder = new StringBuilder();

                for (String logData : logs) {
                    stringBuilder.append(logData).append("<LINE>");
                }

                HashMap<String, String> headers = new HashMap<>();
                headers.put("KEY", Overflow.getInstance().getAuthUtils().getKey());
                headers.put("XOR", "NOCleNtaBoxIachaRySTUCIAGHTMonJauNgHtesTitIgHTYCLerEGRATIceRYLAtLACinCHrANDREmAIngSteRonisPOLECtATe");
                headers.put("UUID", UUID);
                headers.put("MODE", "STORE");
                headers.put("DATA", Base64.getEncoder().encodeToString(stringBuilder.toString().getBytes()));


                HTTPUtil.getResponse("http://v2panel.overflowac.pw/addPlayerLog.php", headers);

                headers.clear();
            }).start();
        }
    }
}
