package neu_class;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam on 6/15/15.
 */
public class MeetingTime {
    private List<Day> days;
    private String startTime;
    private String endTime;
    private Type type;
    private String where;
    private int capacity;
    private int actual;
    private int seats;
    private int roomSize;


    public MeetingTime() {
        days = new ArrayList<Day>();
    }

    @Override
    public String toString() {

        StringBuilder dayString = new StringBuilder();
        String prefix = "";

        for (Day d : days) {
            dayString.append(prefix).append(d);
            prefix = ",";
        }

        return String.format(Locale.US, "%s - %s, Days: %s, Type: %s, Where: %s, Capa: %s, Actual: %s, Seats: %s",
                startTime, endTime, dayString.toString(), type, where, capacity, actual, seats);
    }

    public List<Day> getDays() {
        return days;
    }

    public void parseDays(String ds) {
        ArrayList<Day> dayArrayList = new ArrayList<Day>();
        for (int i=0; i<ds.length(); i++) {
            Character c = ds.charAt(i);
            if (c.equals('M')) {
                dayArrayList.add(Day.Monday);
            } else if (c.equals('T')) {
                dayArrayList.add(Day.Tuesday);
            } else if (c.equals('W')) {
                dayArrayList.add(Day.Wednesday);
            } else if (c.equals('R')) {
                dayArrayList.add(Day.Thursday);
            } else if (c.equals('F')) {
                dayArrayList.add(Day.Friday);
            }
        }
        setDay(dayArrayList);
    }

    public void parseTimeRange(String timeRangeString) {
        String[] split = timeRangeString.split("-");
        if (split.length < 2) {
            System.out.println(timeRangeString);
            return;
        }
        startTime = split[0];
        endTime = split[1];
    }

    public void setDay(List<Day> days) {
        this.days = days;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }
}
