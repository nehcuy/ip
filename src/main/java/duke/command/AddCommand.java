package duke.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import duke.common.Messages;
import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;
import duke.ui.Ui;
import duke.util.TaskList;

/**
 * Represents a command to add a task to the tasklist; subclass of Command.
 * @author Huang Yuchen
 * @author hyuchen@u.nus.edu
 */
public class AddCommand extends Command {
    private static final String TASK_ADDED = "I've added this task for you! :>\n";
    private final ArrayList<String> words;
    private final String firstWord;

    /**
     * Constructor for AddCommand.
     *
     * @param words the remaining user input after the first keyword
     * @param firstWord the first word in the user input
     */
    public AddCommand(ArrayList<String> words, String firstWord) {
        this.words = words;
        this.firstWord = firstWord;
    }

    /**
     * Executes the command for "todo", "deadline" and "event" keywords.
     * This is the main way for outputting bot replies.
     *
     * @param storage the storage object
     * @param tasklist the task list object
     * @param ui the user interface object
     * @throws DukeException if the user input is unrecognised
     */
    public String execute(Storage storage, TaskList tasklist, Ui ui) throws DukeException {
        String input = String.join(" ", words);
        StringBuilder output = new StringBuilder();
        switch (firstWord) {
        case "todo":
            if (words.size() != 0) {
                Todo todo = new Todo(input);
                tasklist.addTask(todo);
                output.append(TASK_ADDED)
                        .append(todo).append("\n")
                        .append("You have ").append(tasklist.tasks.size())
                        .append((tasklist.tasks.size() == 1 ? " task! :D" : " tasks! :D"));
            } else {
                throw new DukeException("Please enter a task following 'todo' and I'll add it into your list. T^T");
            }
            break;
        case "deadline":
            // After adding new exceptions, throw them here
            String remainingDdlWords = String.join(" ", words.subList(0, words.indexOf("/by")));
            String ddl = String.join(" ", words.subList(words.indexOf("/by") + 1, words.size()));
            LocalDate ddlDate = LocalDate.parse(ddl);
            String newDdl = ddlDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            Deadline deadline;
            if (ddl.matches("\\d{4}-\\d{2}-\\d{2}")) {
                deadline = new Deadline(remainingDdlWords, newDdl);
            } else {
                deadline = new Deadline(remainingDdlWords, ddl);
            }
            tasklist.addTask(deadline);
            output.append(TASK_ADDED)
                    .append(deadline).append("\n")
                    .append("You have ").append(tasklist.tasks.size())
                    .append((tasklist.tasks.size() == 1 ? " task! :D" : " tasks! :D"));
            break;
        case "event":
            // After adding new exceptions, throw them here
            String remainingEventWords = String.join(" ", words.subList(0, words.indexOf("/at")));
            String evt = String.join(" ", words.subList(words.indexOf("/at") + 1, words.size()));
            LocalDate evtDate = LocalDate.parse(evt);
            String newEvt = evtDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            Event event;
            if (evt.matches("\\d{4}-\\d{2}-\\d{2}")) {
                event = new Event(remainingEventWords, newEvt);
            } else {
                event = new Event(remainingEventWords, evt);
            }
            tasklist.addTask(event);
            output.append(TASK_ADDED)
                    .append(event).append("\n")
                    .append("You have ").append(tasklist.tasks.size())
                    .append((tasklist.tasks.size() == 1 ? " task! :D" : " tasks! :D"));
            break;
        default:
            // Defensive coding for default statement.
            output.append(Messages.UNKNOWN_COMMAND);
        }
        return output.toString();
    }
}
