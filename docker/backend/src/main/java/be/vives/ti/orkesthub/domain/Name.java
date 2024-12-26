package be.vives.ti.orkesthub.domain;

import be.vives.ti.orkesthub.domain.request.NameRequest;

public class Name {
    private String first;
    private String last;

    public Name() {}

    public Name(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public Name(NameRequest nameRequest) {
        this.first = nameRequest.getFirst();
        this.last = nameRequest.getLast();
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
