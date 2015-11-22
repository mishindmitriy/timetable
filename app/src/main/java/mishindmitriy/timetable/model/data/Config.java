package mishindmitriy.timetable.model.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mishindmitriy on 22.11.2015.
 */
@DatabaseTable
public class Config {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
    private Thing currentThing = null;
    @DatabaseField(dataType = DataType.ENUM_STRING)
    private PeriodType periodType = PeriodType.TODAY;

    public Config() {

    }

    public Thing getCurrentThing() {
        return currentThing;
    }

    public void setCurrentThing(Thing currentThing) {
        this.currentThing = currentThing;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }
}
