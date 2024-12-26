package be.vives.ti.orkesthub.service;

import be.vives.ti.orkesthub.domain.*;
import be.vives.ti.orkesthub.exception.CategoryNotFoundException;
import be.vives.ti.orkesthub.exception.MemberNotFoundException;
import be.vives.ti.orkesthub.repository.CategoryRepository;
import be.vives.ti.orkesthub.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private MemberService memberService;

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
    void findAllMembers() {

        when(memberRepository.findAll()).thenReturn(List.of(johnDoe, janeDoe));

        // Act
        List<Member> members = memberService.findAll();

        // Assert
        assertThat(members).hasSize(2);
        assertThat(members).extracting(member -> member.getName().getFirst())
                .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void findByIdFound() {
        // Arrange
        johnDoe.setId("ab12354bab41ab12354bab41");

        when(memberRepository.findById("ab12354bab41ab12354bab41")).thenReturn(Optional.of(johnDoe));

        // Act
        Member foundMember = memberService.findById("ab12354bab41ab12354bab41");

        // Assert
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getName().getFirst()).isEqualTo("John");
        verify(memberRepository, times(1)).findById("ab12354bab41ab12354bab41");
    }

    @Test
    void findByIdNotFound() {
        // Arrange
        when(memberRepository.findById("ab12354bab41ab12354bab41")).thenReturn(Optional.empty());

        // Act & Assert
        try {
            memberService.findById("ab12354bab41ab12354bab41");
        } catch (MemberNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Member with id: \"ab12354bab41ab12354bab41\" not found");
        }
        verify(memberRepository, times(1)).findById("ab12354bab41ab12354bab41");
    }

    @Test
    void saveMember() {
        janeDoe.setId("ab12354bab41ab12354bab41");

        when(memberRepository.save(janeDoe)).thenReturn(janeDoe);

        // Act
        Member savedMember = memberService.save(janeDoe);

        // Assert
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isEqualTo("ab12354bab41ab12354bab41");
        verify(memberRepository, times(1)).save(janeDoe);
    }

    @Test
    void deleteMember() {
        johnDoe.setId("ab12354bab41ab12354bab41");

        when(memberRepository.findById("ab12354bab41ab12354bab41")).thenReturn(Optional.of(johnDoe));
        doNothing().when(memberRepository).deleteById("ab12354bab41ab12354bab41");

        // Act
        Member deletedMember = memberService.deleteMember("ab12354bab41ab12354bab41");

        // Assert
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getName().getFirst()).isEqualTo("John");
        verify(memberRepository, times(1)).deleteById("ab12354bab41ab12354bab41");
    }

    @Test
    void findManagementMembers() {
        johnDoe.setManagement(true);
        janeDoe.setManagement(false);

        when(memberRepository.findAll()).thenReturn(List.of(johnDoe, janeDoe));

        // Act
        List<Member> managementMembers = memberService.findManagementMembers();

        // Assert
        assertThat(managementMembers).hasSize(1);
        assertThat(managementMembers).allMatch(Member::isManagement);
    }

    // ---------------- Negative tests ---------------- //

    @Test
    void findByIdThrowsExceptionWhenNotFound() {
        // Arrange: Mock repository to return empty
        String memberId = "invalidId";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert: Verify the exception is thrown
        try {
            memberService.findById(memberId);
        } catch (MemberNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Member with id: \"invalidId\" not found");
        }
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void deleteMemberThrowsExceptionWhenNotFound() {
        // Arrange: Mock repository to return empty
        String memberId = "nonExistentId";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert: Verify the exception is thrown
        try {
            memberService.deleteMember(memberId);
        } catch (MemberNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Member with id: \"nonExistentId\" not found");
        }
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, times(0)).deleteById(memberId);
    }

    @Test
    void saveMemberThrowsExceptionWhenNull() {
        // Act & Assert: Verify that saving a null member throws an exception
        try {
            memberService.save(null);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Member cannot be null");
        }
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    void findManagementMembersReturnsEmptyListWhenNoneExist() {
        // Arrange: Mock repository to return members without management
        johnDoe.setManagement(false);
        janeDoe.setManagement(false);
        when(memberRepository.findAll()).thenReturn(List.of(johnDoe, janeDoe));

        // Act
        List<Member> managementMembers = memberService.findManagementMembers();

        // Assert
        assertThat(managementMembers).isEmpty();
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void findAllMembersReturnsEmptyListWhenNoneExist() {
        // Arrange: Mock repository to return an empty list
        when(memberRepository.findAll()).thenReturn(List.of());

        // Act
        List<Member> members = memberService.findAll();

        // Assert
        assertThat(members).isEmpty();
        verify(memberRepository, times(1)).findAll();
    }

}

