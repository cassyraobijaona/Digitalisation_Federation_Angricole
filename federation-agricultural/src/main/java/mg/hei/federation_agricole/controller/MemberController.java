package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Member> createMembers(
            @RequestBody List<CreateMember> requests) {
        return memberService.createMembers(requests);
    }
}