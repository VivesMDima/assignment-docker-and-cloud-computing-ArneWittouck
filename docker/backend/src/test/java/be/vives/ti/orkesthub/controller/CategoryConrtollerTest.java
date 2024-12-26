package be.vives.ti.orkesthub.controller;

import be.vives.ti.orkesthub.domain.Category;
import be.vives.ti.orkesthub.domain.request.CategoryRequest;
import be.vives.ti.orkesthub.exception.CategoryNotFoundException;
import be.vives.ti.orkesthub.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryConrtollerTest {
    private final String baseUrl = "/api/categories";

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Category stringsCategory;
    private Category brassCategory;

    @BeforeEach
    void setUp() {
        stringsCategory = new Category(
                "Strings",
                "A category for string instruments",
                "strings-icon.png",
                new String[]{"Violin", "Cello", "Guitar"}
        );
        stringsCategory.setId("123");

        brassCategory = new Category(
                "Brass",
                "A category for brass instruments",
                "brass-icon.png",
                new String[]{"Trumpet", "Trombone"}
        );
        brassCategory.setId("456");
    }

    // ---------------- Positive tests ---------------- //
    @Test
    void getAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(stringsCategory, brassCategory);
        when(categoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get(baseUrl))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", equalTo("Strings")))
                .andExpect(jsonPath("$[1].title", equalTo("Brass")));
    }

    @Test
    void getCategoryById() throws Exception {
        when(categoryService.findById("123")).thenReturn(Optional.ofNullable(stringsCategory));

        mockMvc.perform(get(baseUrl + "/123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title", equalTo("Strings")))
                .andExpect(jsonPath("$.description", equalTo("A category for string instruments")));
    }

    @Test
    void getAllInstruments() throws Exception {
        // Arrange: Mock the service to return a list of instruments
        List<String> instruments = Arrays.asList("Violin", "Cello", "Drums", "Guitar");
        when(categoryService.getAllInstruments()).thenReturn(instruments);

        // Act & Assert: Perform a GET request and verify the response
        mockMvc.perform(get(baseUrl + "/instruments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]", equalTo("Violin")))
                .andExpect(jsonPath("$[1]", equalTo("Cello")))
                .andExpect(jsonPath("$[2]", equalTo("Drums")))
                .andExpect(jsonPath("$[3]", equalTo("Guitar")));
    }


    @Test
    void createCategory() throws Exception {
        CategoryRequest newCategoryRequest = new CategoryRequest(
                "Percussion",
                "A category for percussion instruments",
                "percussion-icon.png",
                new String[]{"Drums", "Tambourine"}
        );

        when(categoryService.save(any(Category.class))).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setId("789");
            return savedCategory;
        });

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newCategoryRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo("789")))
                .andExpect(jsonPath("$.title", equalTo("Percussion")));
    }

    @Test
    void updateCategory() throws Exception {
        CategoryRequest updateRequest = new CategoryRequest(
                "Updated Strings",
                "Updated description",
                "updated-strings-icon.png",
                new String[]{"Updated Violin"}
        );

        when(categoryService.findById("abcde12345abcde12345abcd")).thenReturn(Optional.ofNullable(stringsCategory));
        when(categoryService.updateCategory(any(), any())).thenAnswer(invocation -> {
            Category updatedCategory = stringsCategory;
            updatedCategory.setTitle("Updated Strings");
            updatedCategory.setDescription("Updated description");
            return updatedCategory;
        });

        mockMvc.perform(put(baseUrl + "/abcde12345abcde12345abcd")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("Updated Strings")))
                .andExpect(jsonPath("$.description", equalTo("Updated description")));
    }

    @Test
    void deleteCategory() throws Exception {
        when(categoryService.deleteCategory("123")).thenReturn(stringsCategory);

        mockMvc.perform(delete(baseUrl + "/123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("Strings")));
    }

    // ---------------- Negative tests ---------------- //

    @Test
    void getCategoryByIdNotFound() throws Exception {
        when(categoryService.findById("999")).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(get(baseUrl + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllInstruments_NoInstrumentsFound() throws Exception {
        // Arrange: Mock the service to return an empty list
        when(categoryService.getAllInstruments()).thenReturn(List.of());

        // Act & Assert: Perform a GET request and verify the response
        mockMvc.perform(get(baseUrl + "/instruments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0))); // Expect an empty list
    }


    @Test
    void createCategoryValidationError() throws Exception {
        CategoryRequest invalidCategoryRequest = new CategoryRequest(
                "",
                "",
                null,
                null
        );

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidCategoryRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategoryNotFound() throws Exception {
        CategoryRequest updateRequest = new CategoryRequest(
                "Non-existent",
                "Non-existent description",
                "non-existent-icon.png",
                new String[]{"Non-existent instrument"}
        );

        when(categoryService.findById("abcde12345abcde12345abcd")).thenThrow(new CategoryNotFoundException("Category not found"));
        when(categoryService.updateCategory(any(), any())).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(put(baseUrl + "/abcde12345abcde12345abcd")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategoryNotFound() throws Exception {
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryService).deleteCategory("999");

        mockMvc.perform(delete(baseUrl + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
