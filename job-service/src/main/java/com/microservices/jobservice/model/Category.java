package com.microservices.jobservice.model;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "categories")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category extends BaseEntity {
    private String name;
    private String description;
    private String imageId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Job> jobs;

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageId='" + imageId + '\'' +
                ", jobs=" + jobs +
                '}';
    }
}