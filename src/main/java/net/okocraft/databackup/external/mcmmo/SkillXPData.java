package net.okocraft.databackup.external.mcmmo;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.configapi.common.configurable.Configurable;
import com.github.siroshun09.configapi.common.configurable.IntegerValue;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.data.impl.BackupTimeValue;
import net.okocraft.databackup.data.impl.UUIDValue;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SkillXPData implements DataType<Integer> {

    private final PrimarySkillType skill;
    private final String dataName;
    private final IntegerValue skillData;

    public SkillXPData(@NotNull PrimarySkillType skill) {
        this.skill = skill;
        this.dataName = "mcmmo-" + skill.getName();
        this.skillData = Configurable.create("mcmmo." + skill.getName(), 0);
    }

    @Override
    public @NotNull String getName() {
        return dataName;
    }

    @Override
    public @NotNull Function<Player, BackupData<Integer>> backup() {
        return player ->
                BackupData.create(
                        player.getUniqueId(),
                        ExperienceAPI.getXP(player, skill.getName())
                );
    }

    @Override
    public @NotNull BiConsumer<BackupData<Integer>, Player> rollback() {
        return (xpData, player) -> ExperienceAPI.setXP(player, skill.getName(), xpData.get());
    }

    @Override
    public @NotNull BiConsumer<BackupData<Integer>, BukkitSender> show() {
        return (xpData, sender) ->
                MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_SHOW_MCMMO, sender)
                        .replace(Placeholders.SKILL_XP, xpData.get())
                        .replace(Placeholders.DATE, BackupTimeValue.toLocalDateTime(xpData.getBackupTime()))
                        .send(sender);
    }

    @Override
    public @NotNull Function<BukkitYaml, BackupData<Integer>> load() {
        return yaml -> BackupData.create(
                UUIDValue.INSTANCE.getValue(yaml),
                BackupTimeValue.INSTANCE.getValue(yaml),
                skillData.getValue(yaml)
        );
    }

    @Override
    public @NotNull BiConsumer<BackupData<Integer>, BukkitYaml> save() {
        return (xpData, yaml) -> yaml.setValue(skillData, xpData.get());
    }
}
