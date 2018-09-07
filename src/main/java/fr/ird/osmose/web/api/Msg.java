package fr.ird.osmose.web.api;


import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Msg {

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    @XmlEnum(String.class)
    protected enum Type {
        @XmlEnumValue("request") REQUEST,
        @XmlEnumValue("response") RESPONSE
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private Type type;

    public Msg() {}
}
