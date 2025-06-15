package com.mango.diary.diary.controller;

import com.mango.diary.auth.support.AuthUser;

import com.mango.diary.common.enums.Emotion;
import com.mango.diary.diary.dto.*;
import com.mango.diary.diary.service.DiaryService;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/diary")
    public ResponseEntity<DiaryIdDTO> saveDiary(@RequestBody DiaryRequest diaryRequest,
                                       @Parameter(hidden = true) @AuthUser Long userId) {
        return ResponseEntity.ok(diaryService.createDiary(diaryRequest, userId));
    }

    @GetMapping("/diary")
    public ResponseEntity<DiaryDetailResponse> readDiary(@Parameter(hidden = true) @AuthUser Long userId, @RequestParam Long diaryId) {
        return ResponseEntity.ok(diaryService.getDiary(userId ,diaryId));
    }

    @GetMapping("/diary/all")
    public ResponseEntity<Page<DiaryResponse>> getDiaryList(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) Integer emotion,
                                                            @Parameter(hidden = true) @AuthUser Long userId) {

        Emotion emotion2 = (emotion != null) ? Emotion.fromCode(emotion) : null;
        if (emotion == null) {
            return ResponseEntity.ok(diaryService.getAllDiaries(page, size, userId));
        }
        return ResponseEntity.ok(diaryService.getAllDiariesByEmotion(page, size, emotion2, userId));
    }

    @GetMapping("/diary/all/month")
    public ResponseEntity<List<DiaryForCalenderResponse>> readAllDiariesByMonth(@Parameter(description = "yyyy-MM 형태로 입력", example = "2024-08")
                                                                                    @RequestParam YearMonth yearMonth,
                                                                                @Parameter(hidden = true) @AuthUser Long userId) {
        return ResponseEntity.ok(diaryService.getAllDiariesByMonth(yearMonth, userId));
    }

    @DeleteMapping("/diary")
    public ResponseEntity<?> deleteDiary(@RequestBody DiaryIdDTO diaryId,
                                              @AuthUser @Parameter(hidden = true) Long userId) {
        diaryService.deleteDiary(diaryId.diaryId(), userId);
        return new ResponseEntity<>("일기가 삭제되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/diary/search")
    public ResponseEntity<Page<DiaryListDTO>> searchDiary(@RequestParam String keyword,
                                                          @RequestParam int page,
                                                          @RequestParam int size,
                                                          @Parameter(hidden = true) @AuthUser Long userId){
        return ResponseEntity.ok(diaryService.searchDiary(keyword, page, size, userId));
    }
}
