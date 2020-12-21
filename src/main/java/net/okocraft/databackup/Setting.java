package net.okocraft.databackup;

import com.github.siroshun09.configapi.common.configurable.BooleanValue;
import com.github.siroshun09.configapi.common.configurable.IntegerValue;
import com.github.siroshun09.configapi.common.configurable.StringValue;

import static com.github.siroshun09.configapi.common.configurable.Configurable.create;

public class Setting {

    public static final IntegerValue BACKUP_INTERVAL = create("backup.interval", 30);
    public static final IntegerValue BACKUP_PERIOD = create("backup.period", 5);
    public static final StringValue BACKUP_DESTINATION_DIRECTORY = create("backup.destination-directory", "");
    public static final BooleanValue BACKUP_BROADCAST = create("backup.broadcast", false);

    private Setting() {
        throw new UnsupportedOperationException();
    }
}
