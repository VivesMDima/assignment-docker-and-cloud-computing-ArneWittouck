package be.vives.ti.orkesthub.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Categories")
public class Category {

    @Id
    private String id;

    private String title;

    private String description;

    private String icon;

    private String[] instruments;

    public Category() {}

    public Category(String title, String description, String icon, String[] instruments) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.instruments = instruments;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setInstruments(String[] instruments) {
        this.instruments = instruments;
    }
}
