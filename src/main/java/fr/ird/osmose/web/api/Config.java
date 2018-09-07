package fr.ird.osmose.web.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement
public class Config {

    private Integer timeStepsPerYear = 12;
    private List<Group> groups;

    public Config() {
    }

    public Integer getTimeStepsPerYear() {
        return timeStepsPerYear;
    }

    public void setTimeStepsPerYear(Integer timeStepsPerYear) {
        this.timeStepsPerYear = timeStepsPerYear;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}
