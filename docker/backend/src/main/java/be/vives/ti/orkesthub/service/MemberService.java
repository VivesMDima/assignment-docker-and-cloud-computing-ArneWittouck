package be.vives.ti.orkesthub.service;

import be.vives.ti.orkesthub.domain.Category;
import be.vives.ti.orkesthub.domain.Member;
import be.vives.ti.orkesthub.domain.request.MemberUpdateRequest;
import be.vives.ti.orkesthub.exception.CategoryNotFoundException;
import be.vives.ti.orkesthub.exception.MemberNotFoundException;
import be.vives.ti.orkesthub.repository.CategoryRepository;
import be.vives.ti.orkesthub.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public MemberService(MemberRepository memberRepository, CategoryRepository categoryRepository) {
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with id: \"" + id + "\" not found"));
    }

    public Member save(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        return memberRepository.save(member);
    }

    public Member updateMember(String id, MemberUpdateRequest request) {

        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with id: \"" + id + "\" not found"));

        existingMember.setGender(request.getGender());
        existingMember.setName(request.getName());
        existingMember.setAddress(request.getAddress());
        existingMember.setEmail(request.getEmail());
        existingMember.setPhone(request.getPhone());
        existingMember.setBirthdate(request.getBirthdate());
        existingMember.setMemberSince(request.getMemberSince());
        existingMember.setInstruments(request.getInstruments());
        existingMember.setPicture(request.getPicture());
        existingMember.setManagement(request.isManagement());

        return memberRepository.save(existingMember);
    }

    public Member deleteMember(String id) {

        Member toDeleteMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with id: \"" + id + "\" not found"));

        memberRepository.deleteById(id);

        return toDeleteMember;
    }

    public List<Member> findMembersByCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id:\"" + id + "\" not found"));

        List<Member> allMembers = findAll();

        List<String> categoryInstruments = Arrays.stream(category.getInstruments()).toList();
        ArrayList<Member> membersUnderCategory = new ArrayList<>();

        if (category.getInstruments() != null && category.getInstruments().length > 0 && !allMembers.isEmpty()) {
            for (Member member : allMembers) {
                String[] memberInstruments = member.getInstruments();
                for (String memberInstrument : memberInstruments) {
                    if (categoryInstruments.contains(memberInstrument)) {
                        membersUnderCategory.add(member);
                    }
                }
            }
        }

        return membersUnderCategory;
    }

    public List<Member> findMembersByInstrument(String instrument) {

        List<Member> allMembers = findAll();
        ArrayList<Member> membersWithInstrument = new ArrayList<>();

        if (!allMembers.isEmpty()) {
            for (Member member : allMembers) {
                List<String> memberInstruments = Arrays.stream(member.getInstruments()).toList();
                if (!memberInstruments.isEmpty() && memberInstruments.contains(instrument)) {
                    membersWithInstrument.add(member);
                }
            }
        }

        return membersWithInstrument;
    }

    public List<Member> findManagementMembers() {
        List<Member> allMembers = findAll();
        return allMembers.stream().filter(Member::isManagement).toList();
    }
}
