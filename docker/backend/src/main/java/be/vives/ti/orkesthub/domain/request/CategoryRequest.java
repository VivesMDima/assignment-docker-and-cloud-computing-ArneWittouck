package be.vives.ti.orkesthub.domain.request;

import be.vives.ti.orkesthub.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private String icon;

    @NotNull
    private String[] instruments;

    public CategoryRequest() {}

    public CategoryRequest(String title, String description, String icon, String[] instruments) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.instruments = instruments;
    }

    public Category makeCategory() {
        return new Category(title, description, icon, instruments);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String[] getInstruments() {
        return instruments;
    }
}
