package com.juno.simple.board.service;

import com.juno.simple.board.domain.BoardEntity;
import com.juno.simple.board.domain.dto.BoardPostRequest;
import com.juno.simple.board.domain.response.BoardListResponse;
import com.juno.simple.board.domain.response.BoardResponse;
import com.juno.simple.board.repository.BoardRepository;
import com.juno.simple.member.domain.entity.MemberEntity;
import com.juno.simple.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BoardResponse postBoard (BoardPostRequest boardPostRequest) {
        MemberEntity findMember = memberRepository.findById(boardPostRequest.getMemberId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        BoardEntity saveBoard = boardRepository.save(boardPostRequest.toEntity(findMember));
        return BoardResponse.from(saveBoard);
    }

    public BoardListResponse getBoardList(Pageable pageable) {
        Page<BoardEntity> page = boardRepository.findAll(pageable);
        List<BoardResponse> list = page.getContent().stream().map(BoardResponse::from).collect(Collectors.toList());

        return BoardListResponse.builder()
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .last(page.isLast())
                .empty(page.isEmpty())
                .list(list)
                .build();
    }
}