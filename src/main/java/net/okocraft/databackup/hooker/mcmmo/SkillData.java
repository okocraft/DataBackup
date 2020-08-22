package net.okocraft.databackup.hooker.mcmmo;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.BackupData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class SkillData implements BackupData {

    final static String PATH_PREFIX = "mcmmo.";

    private final PrimarySkillType skillType;
    private final int xp;

    SkillData(@NotNull PrimarySkillType skillType, int xp) {
        this.skillType = skillType;
        this.xp = xp;
    }

    @Override
    public void save(@NotNull BukkitYaml yaml) {
        yaml.set(PATH_PREFIX + skillType.getName(), xp);
    }

    @Override
    public void rollback(@NotNull Player player) {
        ExperienceAPI.setXP(player, skillType.getName(), xp);
    }

    @Override
    public void show(@NotNull Player player, @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        Message.COMMAND_SHOW_MCMMO.replaceSkill(skillType.getName()).replaceSkillXp(xp).send(player);
    }
}
