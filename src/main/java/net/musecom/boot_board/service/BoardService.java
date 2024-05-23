package net.musecom.boot_board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.musecom.boot_board.dto.BoardDto;
import net.musecom.boot_board.entity.BoardEntity;
import net.musecom.boot_board.repository.BoardRepository;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public void write(BoardDto bDto) {
        BoardEntity bEntity = BoardEntity.toBoardEntity(bDto);
        boardRepository.save(bEntity);
    }


    public List<BoardDto> findAll(){
        //column에 있는 모든 내용을 가져옴
        List<BoardEntity> bEntities = boardRepository.findAll();

        //BoardDto 타입의 ArrayList를 만듦        
        List<BoardDto> bDtos = new ArrayList<>();

        //위에서 만든 ArrayList 박스에 column에서 가져온 내용을 하나하나 채움
        for(BoardEntity bEntity : bEntities) {
            bDtos.add(BoardDto.toBoardDto(bEntity));
        }
        return bDtos;
    }

    /* 게시글 조회수 증가 */
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id); //repository 받아와 구현
    }

    /* 게시물 보기 */
    public BoardDto findById(Long id){
        //Optional 클래스 값이 있을수도, 없을수도 있는 타입
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
       if(optionalBoardEntity.isPresent()){

          BoardEntity boardEntity = optionalBoardEntity.get();
          BoardDto boardDto = BoardDto.toBoardDto(boardEntity);
          return boardDto;
          
       }else{
         return null;

       }
    }

    /* 게시물 수정 */
    public BoardDto update(BoardDto bDto){
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(bDto);
        boardRepository.save(boardEntity);
        return findById(bDto.getId()); //앞화면으로 되돌아가기 findbyid
    }
}
