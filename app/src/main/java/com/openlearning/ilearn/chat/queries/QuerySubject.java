package com.openlearning.ilearn.chat.queries;

import java.io.Serializable;

public class QuerySubject implements Serializable {

    private int queryID;

    private int appointedUserID;

    private String queryType;

    private int querySequenceNumber;

    private String name;

    private String email;

    public QuerySubject() {

    }

    public QuerySubject(int queryID, int appointedUserID, String queryType, int querySequenceNumber, String name, String email) {
        this.queryID = queryID;
        this.appointedUserID = appointedUserID;
        this.queryType = queryType;
        this.querySequenceNumber = querySequenceNumber;
        this.name = name;
        this.email = email;
    }

    public int getQueryID() {
        return queryID;
    }

    public void setQueryID(int queryID) {
        this.queryID = queryID;
    }

    public int getAppointedUserID() {
        return appointedUserID;
    }

    public void setAppointedUserID(int appointedUserID) {
        this.appointedUserID = appointedUserID;
    }

    public int getQuerySequenceNumber() {
        return querySequenceNumber;
    }

    public void setQuerySequenceNumber(int querySequenceNumber) {
        this.querySequenceNumber = querySequenceNumber;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "QuerySubject{" +
                "queryID=" + queryID +
                ", appointedUserID=" + appointedUserID +
                ", queryType='" + queryType + '\'' +
                ", querySequenceNumber=" + querySequenceNumber +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
