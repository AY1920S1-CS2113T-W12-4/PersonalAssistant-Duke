package Parser;

import Commands.AddCommand;
import Commands.Command;
import Commands.DeleteCommand;
import Interface.*;
import Tasks.Deadline;
import Tasks.Event;
import Tasks.TaskList;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteParse extends Parse {
    private static String[] split;
    private static String[] split1;
    private static String[] split2;
    private static String fullCommand;
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());
    private static LookupTable LT;

    static {
        try {
            LT = new LookupTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DeleteParse(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    @Override
    public Command execute() throws Exception {
        if (fullCommand.trim().substring(0, 8).equals("delete/e")) {
            try { //add/e module_code description /at date from time to time
                String activity = fullCommand.trim().substring(8);
                split = activity.split("/at"); //split[0] is " module_code description", split[1] is "date from time to time"
                if (split[0].trim().isEmpty()) {
                    throw new DukeException("\u2639" + " OOPS!!! The description of a event cannot be empty.");
                }
                split1 = split[1].split("/from"); //split1[0] is "date", split1[1] is "time to time"
                String weekDate = "";
                split2 = split1[0].trim().split(" ");
                weekDate = split2[0];
                if (weekDate.equalsIgnoreCase("reading") || weekDate.equalsIgnoreCase("exam")
                        || weekDate.equalsIgnoreCase("week") || weekDate.equalsIgnoreCase("recess")) {
                    weekDate = LT.getDate(split1[0].trim());
                } else {
                    weekDate = split1[0].trim();
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); //format date
                Date date = formatter.parse(weekDate.trim());
                split2 = split1[1].split("/to"); //split2[0] is (start) "time", split2[1] is (end) "time"
                SimpleDateFormat formatter1 = new SimpleDateFormat("HHmm"); //format time
                Date startTime = formatter1.parse(split2[0].trim());
                Date endTime = formatter1.parse(split2[1].trim());
                SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String dateString = dateFormat.format(date);
                String startTimeString = timeFormat.format(startTime);
                String endTimeString = timeFormat.format(endTime);
                return new DeleteCommand("event", new Event(split[0].trim(), dateString, startTimeString, endTimeString));
            } catch (ParseException | ArrayIndexOutOfBoundsException e) {
                LOGGER.log(Level.INFO, e.toString(), e);
                throw new DukeException("OOPS!!! Please enter in the format as follows:\n" +
                        "delete/e mod_code name_of_event /at dd/MM/yyyy /from HHmm /to HHmm\n" +
                        "or delete/e mod_code name_of_event /at week x day /from HHmm /to HHmm\n");
            }
        } else if (fullCommand.trim().substring(0, 8).equals("delete/d")) {
            try {
                String activity = fullCommand.trim().substring(8);
                split = activity.split("/by");
                if (split[0].trim().isEmpty()) {
                    throw new DukeException("\u2639" + " OOPS!!! The description of a deadline cannot be empty.");
                }
                String weekDate = "";
                split2 = split[1].trim().split(" ");
                weekDate = split2[0];
                if (weekDate.equalsIgnoreCase("reading") || weekDate.equalsIgnoreCase("exam")
                        || weekDate.equalsIgnoreCase("week") || weekDate.equalsIgnoreCase("recess")) {
                    weekDate = split[1].substring(0, split[1].length() - 4); // week x day y
                    String time = split[1].substring(split[1].length() - 4); // time E.g 0300
                    weekDate = LT.getDate(weekDate) + " " + time;
                } else {
                    weekDate = split[1];
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HHmm");
                Date date = formatter.parse(weekDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yyyy");
                String dateString = dateFormat.format(date);
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String timeString = timeFormat.format(date);
                return new DeleteCommand("deadline", new Deadline(split[0].trim(), dateString, timeString));

            } catch (ParseException | ArrayIndexOutOfBoundsException e) {
                LOGGER.log(Level.INFO, e.toString(), e);
                throw new DukeException("OOPS!!! Please enter in the format as follows:\n" +
                        "delete/d mod_code name_of_event /by dd/MM/yyyy HHmm\n" +
                        "or delete/d mod_code name_of_event /by week x day HHmm\n");
            }
        } else {
            throw new DukeException("\u2639" + " OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }
}