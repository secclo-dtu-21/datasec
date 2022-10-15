package dk.dtu.server.entity;

import dk.dtu.server.PrinterServant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Printer {

    private String name;
    private final List<String> queue;
    private boolean status;
    private static final Logger logger = LogManager.getLogger(Printer.class);

    public Printer(String name) {
        this.name = name;
        this.queue = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getQueue() {
        return queue;
    }

    public void addFile(String filename) {
        queue.add(filename);
        logger.info(String.format("%s-%s has been added o the printing queue", this.name, filename));
    }

    public void listQueue() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s-The current tasks are: \n", this.name));
        for (int i = 0; i < queue.size(); i++) {
            sb.append(String.format("<%d> <%s>\n", i + 1, queue.get(i)));
        }
        logger.info(sb.toString());
    }

    public void topQueue(int job) {
        String target = queue.remove(job - 1);
        queue.add(0, target);
        logger.info(String.format("%s-The %dth job has been moved to the top", this.name, job));
    }

    public void clearQueue() {
        queue.clear();
        logger.info(String.format("%s-The Queue is cleared", this.name));
    }

    public int getStatus() {
        int size = queue.size();
        logger.info(String.format("%s-There are %d tasks in the Queue", this.name, size));
        return size;
    }
}
