package be.vives.ti.orkesthub.controller;

import be.vives.ti.orkesthub.domain.request.MemberRequest;
import be.vives.ti.orkesthub.domain.request.MemberUpdateRequest;
import be.vives.ti.orkesthub.domain.response.MemberResponse;
import be.vives.ti.orkesthub.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<MemberResponse> getMembers() {
        return memberService.findAll().stream().map(MemberResponse::new).toList();
    }

    @GetMapping("/{id}")
    public MemberResponse getMemberById(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ID format") String id) {
        return new MemberResponse(memberService.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    public List<MemberResponse> getMembersByCategoryId(@PathVariable String categoryId) {
        return memberService.findMembersByCategory(categoryId).stream().map(MemberResponse::new).toList();
    }

    @GetMapping("/instrument/{instrument}")
    public List<MemberResponse> getMembersByInstrument(@PathVariable String instrument) {
        return memberService.findMembersByInstrument(instrument).stream().map(MemberResponse::new).toList();
    }

    @GetMapping("/management")
    public List<MemberResponse> getManagementMembers() {
        return memberService.findManagementMembers().stream().map(MemberResponse::new).toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(@RequestBody @Valid MemberRequest request) {
        return new MemberResponse(memberService.save(request.makeMember()));
    }

    @PutMapping("/{id}")
    public MemberResponse updateMember(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ID format") String id, @RequestBody @Valid MemberUpdateRequest request) {
        return new MemberResponse(memberService.updateMember(id, request));
    }

    @DeleteMapping("/{id}")
    public MemberResponse deleteMember(@PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid ID format") String id) {
        return new MemberResponse(memberService.deleteMember(id));
    }
}
