package net.comorevi.cosse.backup;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main extends PluginBase {
    private String backupDataFolderPath = new File(".").getAbsoluteFile().getParentFile().getParent()+"/Backup/";
    private Config config;

    @Override
    public void onEnable() {
        initConfig();
        if (config.getBoolean("LogRemover")) deleteLogs();
    }

    @Override
    public void onDisable() {
        if (config.getBoolean("AutoBackup")) backUp();
    }

    private void initConfig() {
        if (!new File("./plugins/BackUp", "config.yml").exists()) saveDefaultConfig();
        config = new Config(new File("./plugins/BackUp", "config.yml"), Config.YAML);
    }

    private void deleteLogs() {
        getServer().getLogger().info(TextFormat.LIGHT_PURPLE+"古いログファイルを削除します。");
        File[] files = new File("./logs/").listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().equals("server.log")) files[i].delete();
        }
    }

    private void backUp() {
        getServer().getLogger().info("======= Backup =======");
        getServer().getLogger().info(TextFormat.LIGHT_PURPLE+"バックアップを開始します。");
        //データ保存用ディレクトリの作成
        new File(backupDataFolderPath).mkdir();
        //全てのワールドを保存
        for (int key : getServer().getLevels().keySet()) {
            getServer().getLogger().info("ワールド"+TextFormat.GREEN+getServer().getLevel(key).getName()+TextFormat.WHITE+"を保存しています。");
            getServer().getLevel(key).save(true);
        }
        //サーバーデータを圧縮して保存
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd-HH時mm分");
        String time = df.format(new Date());
        ZipUtil.pack(new File(".").getAbsoluteFile().getParentFile(), new File(backupDataFolderPath + time + ".zip"));

        ArrayList<String> pathList = new ArrayList<>();
        for (String s : config.getStringList("Ignores")) {
            pathList.add(s);
        }
        if (config.getBoolean("IgnorePlugins")) {
            File[] files = new File("./plugins/").listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".jar")) pathList.add("plugins/"+file.getName());
            }
        }
        String[] paths = pathList.toArray(new String[0]);
        try {
            ZipUtil.removeEntries(new File(backupDataFolderPath + time + ".zip"), paths);
        } catch (NullPointerException e) {
            getServer().getLogger().alert("存在しないファイルがバックアップ対象外とするファイルに指定されています。");
        }
        getServer().getLogger().info(TextFormat.LIGHT_PURPLE+"バックアップ完了。");
        getServer().getLogger().info("======================");
    }
}
