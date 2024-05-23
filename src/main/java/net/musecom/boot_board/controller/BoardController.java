package net.musecom.boot_board.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import net.musecom.boot_board.dto.BoardDto;
import net.musecom.boot_board.service.BoardService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;




@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService bService;
    @GetMapping("/")
    public String getList(Model model) {
        System.out.println("list");
        List<BoardDto> bDtoList = bService.findAll();
        model.addAttribute("lists", bDtoList);
        return "list";
    }
    @GetMapping("/{id}")
    public String detailView(@PathVariable(value = "id") Long id, Model model){
        /*
         * 로직?
         * 1. 조회수 증가 -> hit를 1 올리고
         * 2. detail.html을 출력
         */
        bService.updateHits(id); //증가시키고
        BoardDto bDto = bService.findById(id);//검증 후 dto 담기
        model.addAttribute("board", bDto); //model에 담기
        return "detail";
    }
    
 
    @GetMapping("/write")
    public String getWrite() {
        System.out.println("write");
        return "write";
    }
 
    @PostMapping("/write")
    public String setWrite(@ModelAttribute BoardDto bDto) {
        System.out.println("boardDto = " + bDto);
        bService.write(bDto);
        return "redirect:/board/";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable(value = "id") Long id, Model model){
        BoardDto boardDto = bService.findById(id);
        model.addAttribute("board", boardDto);
        return "update";
    }
    /**
     * jsp보드에서는 
     * 1번을 비밀번호 검증 -> 검증끝나면 밑에 1 -> 2 순으로 진행
     * 스프링에서는
     * 1.에서 글쓰고 1-2번 사이에서 검증 후 2번으로 전송
     * 
     * -비번검증구현창 필요
     * 1. detail처럼 findbyid(id)통해 db 읽고 html에 뿌림 (input에 뿌림)
     * 2. 전송 : 기존 값 update하는 구문 만들어 받은 값들로 수정
     */   

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDto bDto, Model model, RedirectAttributes redirectAttributes){
       //비밀번호 검증을 위해 bDto에서 받을 비번과 boardDto에 담겨있는 비번을 비교한다.
       //findbyid로 데이터 꺼내서 boarddto에 담음 
       BoardDto boardDto = bService.findById(bDto.getId());
        if(boardDto.getPass().equals(bDto.getPass())){
            //수정로직 처리
            BoardDto board = bService.update(bDto);
            model.addAttribute("board", board);
            return "detail";
        }else{
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/board/update/" + bDto.getId();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteForm(@PathVariable(value = "id") Long id, Model model) {
        
        model.addAttribute("id", id);
        return "deleteForm";
        //1. 비밀번호 검증 성공, 실패 로직 작성


    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, @RequestParam String pass, RedirectAttributes redirectAttributes, Model model) {
        //1. pass에 값이 있는지?
        if(pass == null || pass.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "비밀번호를 입력하세요.");
            return "redirect:delete/" + id;
        }
        //2. id와 pass가 db와 같은지
        BoardDto boardDto = bService.findById(id);
        if(boardDto != null && boardDto.getPass().equals(pass)){
            //게시물 삭제 로직
        return "redirect:list";
        }else{
            redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
            return "redirect:delete/" + id;
        }
    }
    
    
}
