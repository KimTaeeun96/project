package com.showmual.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showmual.model.NoticeVo;
import com.showmual.service.NoticeService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/closet")
public class NoticeController {
    
    @Autowired
    NoticeService noticeService;
    
    @Autowired
    NoticeVo noticeVo;
    
    List<NoticeVo> noticeList = new ArrayList<NoticeVo>();
    
    // 공지사항 페이지
    @RequestMapping(value = "/notice", method = RequestMethod.GET)
    public String getNoticeList(Model model) {

        noticeList = noticeService.listNotice();

        for (NoticeVo n : noticeList) {
            System.out.println(n);
        }
        
        model.addAttribute("noticeList", noticeList);
        return "notice";
    }
    
    // 공지사항 새글쓰기
    @RequestMapping("/noticeUpload")
    public String writeNotice() {
        return "noticeUpload";
    }
    
    // 공지사항 글 추가
    @RequestMapping(value = "/addNotice", method = RequestMethod.POST)
    public String addNotice(@RequestParam(value = "title") String title,
                            @RequestParam(value = "contents") String contents,
                            MultipartFile files) throws Exception {

        if (!files.isEmpty()) { // 업로드할 파일이 존재할 경우에만
            String sourceFileName = files.getOriginalFilename();
            String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
            File destinationFile;
            String destinationFileName;
            // 경로
            String fileUrl = "E:/Project/images/notice/"; // 커뮤니티 폴더 생성
            
            do {
                destinationFileName = RandomStringUtils.randomAlphanumeric(16) + "." + sourceFileNameExtension;
                destinationFile = new File(fileUrl + destinationFileName);
            } while (destinationFile.exists());
            
            destinationFile.getParentFile().mkdirs();
            files.transferTo(destinationFile);
            
            // DB에 데이터 넣기
            noticeVo.setTitle(title);
            noticeVo.setContents(contents);
            noticeVo.setImagePath(fileUrl);
            noticeVo.setImageName(destinationFileName);
            
            noticeService.addNotice(noticeVo);
        }

        return "redirect:/closet/notice"; // client ---> server addArticle로 들어온거.
    }

    // 공지사항 상세보기
    @RequestMapping(value = "/viewNotice", method = RequestMethod.GET)
    public ModelAndView viewNotice(@RequestParam(value = "noticeNo") int noticeNo) {
        noticeVo = noticeService.viewNotice(noticeNo);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewNotice");
        mv.addObject("viewNotice", noticeVo);
        
        System.out.println(mv);
        
        return mv;
    }
    
    // 공지사항 글 수정페이지
    @RequestMapping(value = "/editNotice", method = RequestMethod.GET)
    public ModelAndView editNotice(@RequestParam(value = "noticeNo") int noticeNo) {
        noticeVo = noticeService.viewNotice(noticeNo);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("editNotice");
        mv.addObject("editNotice", noticeVo);
        
        System.out.println(mv);
        
        return mv;
    }
    
    // 공지사항 글 수정
    @RequestMapping(value = "/doEditNotice", method = RequestMethod.POST)
    public String doEditNotice(@RequestParam(value = "noticeNo") String noticeNo,
            @RequestParam(value = "title") String title, @RequestParam(value = "contents") String contents,
            // redirect 할 때 파라미터 값을 가지고 가야할 경우에는 RedirectAttributes를 사용한다.
            RedirectAttributes redirect) throws Exception {
        
        noticeVo.setNoticeNo(Integer.parseInt(noticeNo));
        noticeVo.setTitle(title);
        noticeVo.setContents(contents);
        
        noticeService.editNotice(noticeVo);
        
        // viewArticle은 파라미터 값이 필요하므로 위에서 만들어 둔 redirect를 사용한다.
        redirect.addAttribute("noticeNo", noticeNo);
        return "redirect:viewNotice";
    }

    // 공지사항 글 삭제
    @RequestMapping(value = "/removeNotice", method = RequestMethod.GET)
    public String removeNotice(@RequestParam int noticeNo) {
        noticeService.removeNotice(noticeNo);
        return "redirect:notice";
    }
    
}
