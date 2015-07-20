package mishindmitriy.timetable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Group {
    private final String groupID;
    private final String groupName;

    public Group(String groupID, String groupName) {
        this.groupID = groupID;
        this.groupName = groupName;
    }

    public String getGroupID()
    {
       return groupID;
    }

    public String getGroupName()
    {
        return groupName;
    }
}
