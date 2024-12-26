package be.vives.ti.orkesthub.service;

import be.vives.ti.orkesthub.domain.Category;
import be.vives.ti.orkesthub.domain.Member;
import be.vives.ti.orkesthub.domain.request.CategoryRequest;
import be.vives.ti.orkesthub.exception.CategoryNotFoundException;
import be.vives.ti.orkesthub.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(String id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(String id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: \"" + id + "\" not found"));

        existingCategory.setTitle(request.getTitle());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setIcon(request.getIcon());
        existingCategory.setInstruments(request.getInstruments());

        return categoryRepository.save(existingCategory);
    }

    public Category deleteCategory(String id) {

        Category toDeleteCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: \"" + id + "\" not found"));

        categoryRepository.deleteById(id);

        return toDeleteCategory;
    }

    public List<String> getAllInstruments() {
        return categoryRepository.findAll()
                .stream()
                .filter(category -> category.getInstruments() != null)
                .flatMap(category -> List.of(category.getInstruments()).stream())
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
    }
}
