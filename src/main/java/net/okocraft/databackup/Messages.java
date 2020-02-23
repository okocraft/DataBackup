package net.okocraft.databackup;

import com.github.siroshun09.sirolibrary.config.BukkitConfig;
import com.github.siroshun09.sirolibrary.message.BukkitMessage;
import net.okocraft.databackup.data.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;

public class Messages extends BukkitConfig {
    private final static Messages INSTANCE = new Messages();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    private Messages() {
        super(DataBackup.get(), "messages.yml", true);
    }

    public static void init() {
    }

    @NotNull
    public static Messages get() {
        return INSTANCE;
    }

    public void sendTargetAppliedInventory(@NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(target, getString("cmd.info.apply.inventory.target", "&b%date%&7 時点のインベントリに戻されました。")
                .replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendSenderAppliedInventory(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.info.apply.inventory.sender", "&b%target%&7 のインベントリを &b%date%&7 時点に戻しました。")
                .replace("%target%", target.getName()).replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendTargetAppliedEnderChest(@NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(target, getString("cmd.info.apply.ender-chest.target", "&b%date%&7 時点のエンダーチェストに戻されました。")
                .replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendSenderAppliedEnderChest(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.info.apply.ender-chest.sender", "&b%target%&7 のエンダーチェストを &b%date%&7 時点に戻しました。")
                .replace("%target%", target.getName()).replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendTargetAppliedMoney(@NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(target, getString("cmd.info.apply.money.target", "&b%date%&7 時点の所持金に戻されました。")
                .replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendSenderAppliedMoney(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.info.apply.money.sender", "&b%target%&7 の所持金を &b%date%&7 時点に戻しました。")
                .replace("%target%", target.getName()).replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendTargetAppliedXP(@NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(target, getString("cmd.info.apply.xp.target", "&b%date%&7 時点の経験値に戻されました。")
                .replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendSenderAppliedXP(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.info.apply.xp.sender", "&b%target%&7 の経験値を &b%date%&7 時点に戻しました。")
                .replace("%target%", target.getName()).replace("%date%", formatter.format(data.getDateTime())));
    }

    public void sendMoneyBackup(@NotNull CommandSender sender, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.info.show.money", "&b%date%&7 時点の所持金: &b%amount%円")
                .replace("%date%", formatter.format(data.getDateTime())).replace("%amount%", String.valueOf(data.getMoney())));
    }

    public void sendXpBackup(@NotNull CommandSender sender, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.info.show.xp", "&b%date%&7 時点の経験値: &b%amount%")
                .replace("%date%", formatter.format(data.getDateTime())).replace("%amount%", String.valueOf(data.getXp())));
    }

    public void sendForcedBackup(@NotNull CommandSender sender, @NotNull Player target) {
        sendMessageWithPrefix(sender,
                getString("cmd.info.forced-backup", "&b%target%&7 のデータをバックアップしました。").replace("%target%", target.getName()));
    }

    public void sendBackupCmdHelp(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender,
                getString("cmd.help.backup", "&b/db backup <target>&8: &7プレイヤーのバックアップを取ります。"));
    }

    public void sendRollbackCmdHelp(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender,
                getString("cmd.help.rollback", "&b/db rollback <type> <target> <file>&8: &7指定したデータを戻します。"));
    }

    public void sendShowCmdHelp(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender,
                getString("cmd.help.show", "&b/db show {offline} <type> <target> <file>&8: &7指定したデータの内訳を表示します。"));
    }

    public void sendHelp(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender, "Version: &e" + DataBackup.get().getDescription().getVersion());
        sendMessageWithPrefix(sender, "");
        sendBackupCmdHelp(sender);
        sendRollbackCmdHelp(sender);
        sendShowCmdHelp(sender);
    }

    public void sendNotEnoughArgs(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender, getString("cmd.error.not-enough-args", "&c引数が足りません。 &b/db help"));
    }

    public void sendOnlyPlayer(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender, getString("cmd.error.only-player", "&cこのコマンドはプレイヤーのみ実行できます。"));
    }

    public void sendNoPermission(@NotNull CommandSender sender, @NotNull Permission perm) {
        sendMessageWithPrefix(sender,
                getString("cmd.error.no-permission", "&cコマンドを実行する権限がありません: %perm%").replace("%perm%", perm.getName()));
    }

    public void sendPlayerNotFound(@NotNull CommandSender sender, @NotNull String playerName) {
        sendMessageWithPrefix(sender,
                getString("cmd.error.player-not-found", "&c指定したプレイヤー &b%player%&c が見つかりません。").replace("%player%", playerName));
    }

    public void sendBackupNotFound(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender, getString("cmd.error.backup-not-found", "&c指定したバックアップは存在しません。"));
    }

    public void sendEconomyDisabled(@NotNull CommandSender sender) {
        sendMessageWithPrefix(sender, getString("cmd.error.economy-disabled", "このサーバーでは経済が使用できません。"));
    }

    public void sendInvalidDataType(@NotNull CommandSender sender, String type) {
        sendMessageWithPrefix(sender, getString("cmd.error.invalid-data-type", "無効なデータタイプです: %type%").replace("%type%", type));
    }

    public void sendFailedToApplyBackup(@NotNull CommandSender sender, @NotNull PlayerData data) {
        sendMessageWithPrefix(sender, getString("cmd.error.failed-to-apply-backup", "&b%date%&7 時点のバックアップを適用できませんでした。")
                .replace("%date%", formatter.format(data.getDateTime())));
    }

    public String getPrefix() {
        return getString("prefix", "&8[&6DataBackup&8]&7 ");
    }

    private void sendMessageWithPrefix(@NotNull CommandSender sender, @NotNull String msg) {
        sendMessage(sender, getPrefix() + msg);
    }

    private void sendMessage(@NotNull CommandSender sender, @NotNull String msg) {
        BukkitMessage.sendMessageWithColor(sender, msg);
    }
}
