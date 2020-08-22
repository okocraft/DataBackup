package net.okocraft.databackup.hooker.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.okocraft.databackup.data.BackupStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class McMMORegister {

    public static void register(@NotNull BackupStorage storage) {
        Arrays.stream(PrimarySkillType.values())
                .filter(s -> !s.isChildSkill())
                .map(SkillDataType::new)
                .forEach(storage::registerDataType);
    }
}
