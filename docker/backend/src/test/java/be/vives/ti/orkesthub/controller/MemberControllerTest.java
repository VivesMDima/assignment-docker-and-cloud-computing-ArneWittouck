package be.vives.ti.orkesthub.controller;

import be.vives.ti.orkesthub.domain.Address;
import be.vives.ti.orkesthub.domain.Gender;
import be.vives.ti.orkesthub.domain.Member;
import be.vives.ti.orkesthub.domain.Name;
import be.vives.ti.orkesthub.domain.request.AddressRequest;
import be.vives.ti.orkesthub.domain.request.MemberUpdateRequest;
import be.vives.ti.orkesthub.domain.request.NameRequest;
import be.vives.ti.orkesthub.exception.MemberNotFoundException;
import be.vives.ti.orkesthub.repository.MemberRepository;
import be.vives.ti.orkesthub.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    private final String baseUrl = "/api/members";

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private MemberRepository memberRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private Member johnDoe;
    private Member janeDoe;

    @BeforeEach
    void setUp() {
        Date johnBirthDate = new Date(1672531200000L);
        Date johnMemberSince = new Date(1672531200000L);

        Name johnName = new Name("John", "Doe");
        Address johnAddress = new Address("Johnstreet", "20a", "Kortrijk", 8500);
        String[] johnInstruments = new String[]{"Cello", "Drums"};

        Name janeName = new Name("Jane", "Doe");
        Address janeAddress = new Address("Janestreet", "1", "Brugge", 8000);
        String[] janeInstruments = new String[]{};

        johnDoe = new Member(Gender.MALE, johnName, johnAddress, "john@test.be", "+32 472 58 96 32", johnBirthDate, johnMemberSince, johnInstruments, "", false);
        janeDoe = new Member(Gender.FEMALE, janeName, janeAddress, "jane@test.be", "+32 471 54 86 97", johnBirthDate, johnBirthDate, janeInstruments, "", true);

    }

    // ---------------- Positive tests ---------------- //

    @Test
    void getAllMembers() throws Exception {
        List<Member> members = Arrays.asList(johnDoe);
        when(memberService.findAll()).thenReturn(members);

        mockMvc.perform(get(baseUrl))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Check the size of the returned list
                .andExpect(jsonPath("$[0].name.first", equalTo("John")))
                .andExpect(jsonPath("$[0].name.last", equalTo("Doe")))
                .andExpect(jsonPath("$[0].gender", equalTo("MALE")))
                .andExpect(jsonPath("$[0].address.street", equalTo("Johnstreet")))
                .andExpect(jsonPath("$[0].address.number", equalTo("20a")))
                .andExpect(jsonPath("$[0].address.city", equalTo("Kortrijk")))
                .andExpect(jsonPath("$[0].address.postalcode", equalTo(8500)))
                .andExpect(jsonPath("$[0].email", equalTo("john@test.be")))
                .andExpect(jsonPath("$[0].phone", equalTo("+32 472 58 96 32")))
                .andExpect(jsonPath("$[0].instruments", hasSize(2)))
                .andExpect(jsonPath("$[0].instruments[0]", equalTo("Cello")))
                .andExpect(jsonPath("$[0].instruments[1]", equalTo("Drums")));
    }

    @Test
    void getMemberById() throws Exception {
        String memberId = "ab12354bab41ab12354bab41";
        johnDoe.setId(memberId);

        when(memberService.findById(memberId)).thenReturn(johnDoe);

        mockMvc.perform(get(baseUrl + "/" + memberId))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(memberId)))
                .andExpect(jsonPath("$.name.first", equalTo("John")))
                .andExpect(jsonPath("$.name.last", equalTo("Doe")))
                .andExpect(jsonPath("$.gender", equalTo("MALE")))
                .andExpect(jsonPath("$.address.street", equalTo("Johnstreet")))
                .andExpect(jsonPath("$.address.number", equalTo("20a")))
                .andExpect(jsonPath("$.address.city", equalTo("Kortrijk")))
                .andExpect(jsonPath("$.address.postalcode", equalTo(8500)))
                .andExpect(jsonPath("$.email", equalTo("john@test.be")))
                .andExpect(jsonPath("$.phone", equalTo("+32 472 58 96 32")))
                .andExpect(jsonPath("$.instruments", hasSize(2)))
                .andExpect(jsonPath("$.instruments[0]", equalTo("Cello")))
                .andExpect(jsonPath("$.instruments[1]", equalTo("Drums")));
    }

    @Test
    void getManagementMembers() throws Exception {
        List<Member> managementMembers = Arrays.asList(janeDoe);
        when(memberService.findManagementMembers()).thenReturn(managementMembers);

        mockMvc.perform(get(baseUrl + "/management"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name.first", equalTo("Jane")))
                .andExpect(jsonPath("$[0].name.last", equalTo("Doe")))
                .andExpect(jsonPath("$[0].management", equalTo(true)));
    }

    @Test
    void getMembersByInstrument() throws Exception {
        String instrument = "Cello";
        List<Member> membersWithCello = Arrays.asList(johnDoe); // John plays Cello
        when(memberService.findMembersByInstrument(instrument)).thenReturn(membersWithCello);

        mockMvc.perform(get(baseUrl + "/instrument/" + instrument))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1))) // Expect 1 result
                .andExpect(jsonPath("$[0].name.first", equalTo("John")))
                .andExpect(jsonPath("$[0].name.last", equalTo("Doe")))
                .andExpect(jsonPath("$[0].instruments", hasSize(2)))
                .andExpect(jsonPath("$[0].instruments[0]", equalTo("Cello")))
                .andExpect(jsonPath("$[0].instruments[1]", equalTo("Drums")));
    }

    @Test
    void getMembersByCategory() throws Exception {
        String categoryId = "012345677701234567771234";
        List<Member> stringMembers = Arrays.asList(johnDoe);
        when(memberService.findMembersByCategory(categoryId)).thenReturn(stringMembers);

        mockMvc.perform(get(baseUrl + "/category/" + categoryId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1))) // Expect 1 result
                .andExpect(jsonPath("$[0].name.first", equalTo("John")))
                .andExpect(jsonPath("$[0].name.last", equalTo("Doe")))
                .andExpect(jsonPath("$[0].address.street", equalTo("Johnstreet")))
                .andExpect(jsonPath("$[0].address.city", equalTo("Kortrijk")))
                .andExpect(jsonPath("$[0].instruments", hasSize(2)))
                .andExpect(jsonPath("$[0].instruments[0]", equalTo("Cello")))
                .andExpect(jsonPath("$[0].instruments[1]", equalTo("Drums")));
    }

    @Test
    void createMember() throws Exception {
        Member newMember = new Member(
                Gender.FEMALE,
                new Name("Jane", "Smith"),
                new Address("Main Street", "123", "Bruges", 8000),
                "jane.smith@example.com",
                "+32 456 78 90 12",
                new Date(946684800000L),
                new Date(),
                new String[]{"Violin"},
                "",
                false
        );

        when(memberService.save(any(Member.class))).thenAnswer(invocation -> {
            Member savedMember = invocation.getArgument(0);
            savedMember.setId("mockGeneratedId");
            return savedMember;
        });

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMember)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo("mockGeneratedId")))
                .andExpect(jsonPath("$.name.first", equalTo("Jane")))
                .andExpect(jsonPath("$.name.last", equalTo("Smith")))
                .andExpect(jsonPath("$.address.street", equalTo("Main Street")))
                .andExpect(jsonPath("$.address.city", equalTo("Bruges")))
                .andExpect(jsonPath("$.email", equalTo("jane.smith@example.com")))
                .andExpect(jsonPath("$.phone", equalTo("+32 456 78 90 12")))
                .andExpect(jsonPath("$.instruments", hasSize(1)))
                .andExpect(jsonPath("$.instruments[0]", equalTo("Violin")));
    }

    @Test
    void updateMember() throws Exception {
        MemberUpdateRequest request = new MemberUpdateRequest(
                Gender.MALE,
                new NameRequest("Updated", "Doe"),
                new AddressRequest("UpdatedStreet", "20b", "UpdatedCity", 9000),
                "updated@test.be",
                "+32 123 45 67 89",
                new Date(946684800000L), // 2000-01-01
                new Date(1609459200000L), // 2021-01-01
                new String[]{"Piano", "Guitar"},
                "updatedPictureUrl",
                true
        );

        String memberId = "abababababababababababab";
        johnDoe.setId(memberId);
        when(memberService.findById("abababababababababababab")).thenReturn(johnDoe);
        Member updatedJohn = getUpdatedJohn();
        when(memberService.updateMember(any(), any())).thenReturn(updatedJohn);
        when(memberRepository.save(any())).thenReturn(updatedJohn);
        mockMvc.perform(put(baseUrl+"/"+memberId)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name.first", equalTo("Updated")));
    }

    private static Member getUpdatedJohn() {
        Member updatedJohn = new Member(
                Gender.MALE,
                new Name("Updated", "Doe"),
                new Address("UpdatedStreet", "20b", "UpdatedCity", 9000),
                "updated@test.be",
                "+32 123 45 67 89",
                new Date(946684800000L),
                new Date(1609459200000L),
                new String[]{"Piano", "Guitar"},
                "updatedPictureUrl",
                true
        );
        updatedJohn.setId("abababababababababababab");
        return updatedJohn;
    }

    @Test
    void deleteMember() throws Exception {
        Member existingMember = new Member(
                Gender.MALE,
                new Name("John", "Doe"),
                new Address("Johnstreet", "20a", "Kortrijk", 8500),
                "john@test.be",
                "+32 472 58 96 32",
                new Date(946684800000L), // 2000-01-01
                new Date(1609459200000L), // 2021-01-01
                new String[]{"Cello", "Drums"},
                "pictureUrl",
                false
        );
        existingMember.setId("012345677701234567771234");

        // Mock the repository and service behavior
        when(memberRepository.findById("012345677701234567771234")).thenReturn(Optional.of(existingMember));
        doNothing().when(memberRepository).deleteById("012345677701234567771234");
        when(memberService.deleteMember("012345677701234567771234")).thenReturn(existingMember);

        // Act & Assert: Perform a DELETE request and verify the result
        mockMvc.perform(delete(baseUrl + "/012345677701234567771234")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name.first", equalTo("John")))
                .andExpect(jsonPath("$.name.last", equalTo("Doe")))
                .andExpect(jsonPath("$.address.street", equalTo("Johnstreet")))
                .andExpect(jsonPath("$.email", equalTo("john@test.be")))
                .andExpect(jsonPath("$.phone", equalTo("+32 472 58 96 32")));

        // Verify that the deleteById method was called
        verify(memberService, times(1)).deleteMember("012345677701234567771234");
    }



    // ---------------- Negative tests ---------------- //

    @Test
    void getMemberByIdNotFound() throws Exception {
        // Arrange: Return empty Optional to simulate member not found
        String memberId = "abcde12345abcde12345abcd";
        when(memberService.findById(memberId)).thenThrow(new MemberNotFoundException("Member not found"));

        // Act & Assert: Perform GET request for a non-existent member and verify the result
        mockMvc.perform(get(baseUrl + "/" + memberId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getMembersByInstrumentNotFound() throws Exception {
        // Arrange: Return empty list to simulate no members found for instrument
        String instrument = "NonexistentInstrument";
        when(memberService.findMembersByInstrument(instrument)).thenReturn(List.of());

        // Act & Assert: Perform GET request for members by instrument and verify the result
        mockMvc.perform(get(baseUrl + "/instrument/" + instrument))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // Expect empty list
    }

    @Test
    void getMembersByCategoryNotFound() throws Exception {
        // Arrange: Return empty list to simulate no members found for instrument
        String categoryId = "abcde12345abcde12345abcd";
        when(memberService.findMembersByCategory(categoryId)).thenReturn(List.of());

        // Act & Assert: Perform GET request for members by instrument and verify the result
        mockMvc.perform(get(baseUrl + "/category/" + categoryId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // Expect empty list
    }

    @Test
    void updateMemberNotFound() throws Exception {
        MemberUpdateRequest updateRequest = new MemberUpdateRequest(
                Gender.MALE,
                new NameRequest("Updated", "Doe"),
                new AddressRequest("UpdatedStreet", "20b", "UpdatedCity", 9000),
                "updated@test.be",
                "+32 123 45 67 89",
                new Date(946684800000L), // 2000-01-01
                new Date(1609459200000L), // 2021-01-01
                new String[]{"Piano", "Guitar"},
                "updatedPictureUrl",
                true
        );

        when(memberRepository.findById("abababababababababababab")).thenThrow(new MemberNotFoundException("Member not found"));
        when(memberService.updateMember(any(), any())).thenThrow(new MemberNotFoundException("Member not found"));

        // Act & Assert: Perform a PUT request for a non-existent member and verify the result
        mockMvc.perform(put(baseUrl + "/abababababababababababab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMemberNotFound() throws Exception {
        // Arrange: Return empty Optional to simulate member not found
        String memberId = "abcde12345abcde12345abcd";
        doThrow(new MemberNotFoundException("Member not found")).when(memberService).deleteMember(memberId);

        // Act & Assert: Perform DELETE request for a non-existent member and verify the result
        mockMvc.perform(delete(baseUrl + "/" + memberId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
