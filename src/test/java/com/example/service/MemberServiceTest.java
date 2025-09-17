package com.example.service;

import com.example.domain.member.dto.MemberRequestDto;
import com.example.domain.member.dto.MemberResponseDto;
import com.example.domain.member.service.MemberService;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.MemberErrorCode;
import com.example.domain.member.model.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.participation.repository.ParticipationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepo;
    @Mock
    private ParticipationRepository partRepo; //memberDelete 참여 삭제
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MemberService memberService;

    private MemberRequestDto validDto;
    private Member savedMember;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        validDto = new MemberRequestDto(
                "user@example.com",
                "password123",
                "John Doe"
        );
        now = LocalDateTime.now();
        // 빌더를 통해 ID 포함한 엔티티 생성
        savedMember = Member.builder()
                .id(1L)
                .email(validDto.email())
                .password("encodedPwd")
                .name(validDto.name())
                .createdAt(now)
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("register: 성공 - 신규 회원 저장 후 DTO 반환")
    void registerSuccess() {
        when(memberRepo.findByEmail(validDto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validDto.password())).thenReturn("encodedPwd");
        when(memberRepo.save(any(Member.class))).thenReturn(savedMember);

        MemberResponseDto result = memberService.register(validDto);

        assertThat(result.id()).isEqualTo(savedMember.getId());
        assertThat(result.email()).isEqualTo(savedMember.getEmail());
        assertThat(result.name()).isEqualTo(savedMember.getName());
        assertThat(result.createdAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("register: 실패 - 중복 이메일 예외 발생")
    void registerDuplicateEmailThrows() {
        Member existing = Member.builder()
                .id(2L)
                .email(validDto.email())
                .password("encodedPwd")
                .name(validDto.name())
                .createdAt(now)
                .deleted(false)
                .build();
        when(memberRepo.findByEmail(validDto.email())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> memberService.register(validDto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(MemberErrorCode.DUPLICATE_EMAIL));
    }

    @Test
    @DisplayName("get: 실패 - 회원 없을 시 NOT_FOUND_USER")
    void getNotFoundThrows() {
        when(memberRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.get(1L))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(MemberErrorCode.NOT_FOUND_USER));
    }

    @Test
    @DisplayName("delete: 성공 - 소프트 삭제 처리")
    void deleteSoftDeletesMember() {
        Member member = Member.builder()
                .id(2L)
                .email("a@b.com")
                .password("pwd")
                .name("Alice")
                .createdAt(now)
                .deleted(false)
                .build();
        when(memberRepo.findByIdAndDeletedFalse(2L)).thenReturn(Optional.of(member));

        memberService.delete(2L);

        assertThat(member.isDeleted()).isTrue();
    }
}
