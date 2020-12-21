package net.okocraft.databackup.lang;

import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mcmessage.replacer.FunctionalPlaceholder;
import com.github.siroshun09.mcmessage.replacer.Placeholder;
import net.okocraft.databackup.data.DataType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.siroshun09.mcmessage.replacer.FunctionalPlaceholder.create;

public final class Placeholders {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    public static final Placeholder PERMISSION = Placeholder.create("%permission%");
    public static final FunctionalPlaceholder<Argument> PLAYER_NAME = create("%player%", Argument::get);
    public static final FunctionalPlaceholder<Player> PLAYER = create("%player%", HumanEntity::getName);
    public static final FunctionalPlaceholder<Argument> DATA_TYPE_NAME = create("%datatype%", Argument::get);
    public static final FunctionalPlaceholder<DataType<?>> DATA_TYPE = create("%datatype%", DataType::getName);
    public static final FunctionalPlaceholder<Material> MATERIAL = create("%material%", Enum::name);
    public static final FunctionalPlaceholder<Integer> PAGE = create("%page%", String::valueOf);
    public static final FunctionalPlaceholder<LocalDateTime> DATE = create("%date%", FORMATTER::format);
    public static final FunctionalPlaceholder<Float> EXP = create("%exp%", String::valueOf);
    public static final FunctionalPlaceholder<Double> MONEY = create("%money%", String::valueOf);
    public static final FunctionalPlaceholder<Integer> SKILL_XP = create("%skill_xp%", String::valueOf);

    private Placeholders() {
        throw new UnsupportedOperationException();
    }
}
