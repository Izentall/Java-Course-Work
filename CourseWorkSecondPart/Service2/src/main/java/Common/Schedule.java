package Common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.TreeSet;

@JsonAutoDetect
public class Schedule
{
    @JsonDeserialize(as = TreeSet.class)
    public TreeSet<Note> set;

    public Schedule()
    {
        set = new TreeSet<>();
    }

    public Schedule(TreeSet<Note> set)
    {
        if (set != null)
        {
            this.set = set;
        }
        else
        {
            throw new IllegalArgumentException("Set of Notes must be not null");
        }
    }
}
