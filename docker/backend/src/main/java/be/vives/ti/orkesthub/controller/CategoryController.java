package be.vives.ti.orkesthub.controller;

import be.vives.ti.orkesthub.domain.request.CategoryRequest;
import be.vives.ti.orkesthub.domain.response.CategoryResponse;
import be.vives.ti.orkesthub.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getMembers() {
        return categoryService.findAll().stream().map(CategoryResponse::new).toList();
    }

    @GetMapping("/{id}")
    public Optional<CategoryResponse> getCategory(@PathVariable String id) {
        return categoryService.findById(id).map(CategoryResponse::new);
    }

    @GetMapping("/instruments")
    public List<String> getAllInstruments() {
        return categoryService.getAllInstruments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody @Valid CategoryRequest request) {
        return new CategoryResponse(categoryService.save(request.makeCategory()));
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ID format") String id, @RequestBody CategoryRequest request) {
        return new CategoryResponse(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public CategoryResponse deleteCategory(@PathVariable String id) {
        return new CategoryResponse(categoryService.deleteCategory(id));
    }
}
