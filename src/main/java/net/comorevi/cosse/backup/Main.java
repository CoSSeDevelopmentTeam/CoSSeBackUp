package net.comorevi.cosse.backup;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends PluginBase {
    @Override
    public void onDisable() {
        getServer().getLogger().info(TextFormat.LIGHT_PURPLE+"バックアップを開始します。");
        //データ保存用ディレクトリの作成
        new File(new File(".").getAbsoluteFile().getParentFile().getParent()+"/Backup").mkdir();
        //全てのワールドを保存
        for (int key : getServer().getLevels().keySet()) {
            getServer().getLogger().info("ワールド"+TextFormat.GREEN+getServer().getLevel(key).getName()+TextFormat.WHITE+"を保存しています。");
            getServer().getLevel(key).save(true);
        }
        //サバーデータを圧縮して保存
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd-aaahh時mm分");
        ZipUtil.pack(new File(".").getAbsoluteFile().getParentFile(), new File(new File(".").getAbsoluteFile().getParentFile().getParent() + "/Backup/" + df.format(date) + ".zip"));
        getServer().getLogger().info(TextFormat.LIGHT_PURPLE+"バックアップ完了。");
    }
}
