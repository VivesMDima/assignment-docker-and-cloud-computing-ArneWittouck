package be.vives.ti.orkesthub.domain.request;

import be.vives.ti.orkesthub.domain.Name;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NameRequest {
    @NotBlank(message = "Field \"first\" must not be blank")
    private String first;
    @NotBlank(message = "Field \"last\" must not be blank")
    private String last;

    public NameRequest() {}

    public NameRequest(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public Name makeName() {
        return new Name(first, last);
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }
}
