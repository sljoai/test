package com.song.cn.agent.serializer;

public class IdnoAndName {

    private String idno;

    private String name;

    public IdnoAndName() {
        super();
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdnoAndName{" +
                "idno='" + idno + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
