package be.vives.ti.orkesthub.domain.response;

import be.vives.ti.orkesthub.domain.Category;
import org.springframework.data.annotation.Id;

public class CategoryResponse {

    @Id
    private String id;

    private String title;

    private String description;

    private String icon;

    private String[] instruments;

    public CategoryResponse() {}

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.description = category.getDescription();
        this.icon = category.getIcon();
        this.instruments = category.getInstruments();
    }

    public String getId() {
        return id;
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
