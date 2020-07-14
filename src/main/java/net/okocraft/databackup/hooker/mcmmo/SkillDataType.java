package net.okocraft.databackup.hooker.mcmmo;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.DataType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillDataType extends DataType {

    private final static String NAME_PREFIX = "mcmmo-";

    public SkillDataType(@NotNull PrimarySkillType skillType) {
        super(NAME_PREFIX + skillType.getName(),
                yaml -> load(yaml, skillType),
                player -> backup(player, skillType));
    }

    @NotNull
    public static BackupData load(@NotNull BukkitYaml yaml, @NotNull PrimarySkillType skillType) {
        int xp = yaml.getInt(SkillData.PATH_PREFIX + skillType.getName());
        return new SkillData(skillType, xp);
    }

    @NotNull
    public static BackupData backup(@NotNull Player player, @NotNull PrimarySkillType skillType) {
        int xp = ExperienceAPI.getXP(player, skillType.getName());
        return new SkillData(skillType, xp);
    }
}
