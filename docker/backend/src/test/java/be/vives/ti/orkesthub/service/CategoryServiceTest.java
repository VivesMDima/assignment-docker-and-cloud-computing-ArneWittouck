package be.vives.ti.orkesthub.service;

import be.vives.ti.orkesthub.domain.Category;
import be.vives.ti.orkesthub.exception.CategoryNotFoundException;
import be.vives.ti.orkesthub.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category stringsCategory;
    private Category windsCategory;

    @BeforeEach
    void setUp() {
        stringsCategory = new Category("Strings", "Strings Category", "", new String[]{"Violin", "Cello"});
        windsCategory = new Category("Winds", "Winds Category", "", new String[]{"Flute", "Clarinet"});
    }

    // ---------------- Positive tests ---------------- //

    @Test
    void findAllCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(stringsCategory, windsCategory));

        // Act
        List<Category> categories = categoryService.findAll();

        // Assert
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getTitle).containsExactlyInAnyOrder("Strings", "Winds");
    }

    @Test
    void findCategoryByIdFound() {
        // Arrange
        stringsCategory.setId("ab12345cd67890ef");
        when(categoryRepository.findById("ab12345cd67890ef")).thenReturn(Optional.of(stringsCategory));

        // Act
        Optional<Category> foundCategory = categoryService.findById("ab12345cd67890ef");

        // Assert
        assertThat(foundCategory).isNotNull();
        foundCategory.ifPresent(category -> assertThat(category.getTitle()).isEqualTo("Strings"));
        verify(categoryRepository, times(1)).findById("ab12345cd67890ef");
    }

    @Test
    void saveCategory() {
        // Arrange
        stringsCategory.setId("ab12345cd67890ef");
        when(categoryRepository.save(stringsCategory)).thenReturn(stringsCategory);

        // Act
        Category savedCategory = categoryService.save(stringsCategory);

        // Assert
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isEqualTo("ab12345cd67890ef");
        verify(categoryRepository, times(1)).save(stringsCategory);
    }

    @Test
    void deleteCategory() {
        // Arrange
        stringsCategory.setId("ab12345cd67890ef");
        when(categoryRepository.findById("ab12345cd67890ef")).thenReturn(Optional.of(stringsCategory));
        doNothing().when(categoryRepository).deleteById("ab12345cd67890ef");

        // Act
        Category deletedCategory = categoryService.deleteCategory("ab12345cd67890ef");

        // Assert
        assertThat(deletedCategory).isNotNull();
        assertThat(deletedCategory.getTitle()).isEqualTo("Strings");
        verify(categoryRepository, times(1)).deleteById("ab12345cd67890ef");
    }

    @Test
    void getAllInstruments() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(stringsCategory, windsCategory));

        // Act
        List<String> instruments = categoryService.getAllInstruments();

        // Assert
        assertThat(instruments).hasSize(4);
        assertThat(instruments).containsExactlyInAnyOrder("Violin", "Cello", "Flute", "Clarinet");
    }

    // ---------------- Negative tests ---------------- //

    @Test
    void findCategoryByIdNotFound() {
        // Arrange
        String categoryId = "nonexistentId";
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        Optional<Category> foundCategory = categoryService.findById(categoryId);

        // Assert
        assertThat(foundCategory).isEmpty();
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void saveCategory_NullCategory() {
        // Act & Assert
        try {
            categoryService.save(null);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Category cannot be null");
        }

        // Verify
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategoryNotFound() {
        // Arrange
        String categoryId = "nonexistentId";
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            categoryService.deleteCategory(categoryId);
        } catch (CategoryNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Category with id: \"nonexistentId\" not found");
        }

        // Verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(anyString());
    }

    @Test
    void getAllInstruments_NoCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<String> instruments = categoryService.getAllInstruments();

        // Assert
        assertThat(instruments).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }
}

