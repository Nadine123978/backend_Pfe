package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Folder;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/folder")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class FolderController {

	@Autowired
	private EventRepository eventRepository;
	
    @Autowired
    private FolderRepository folderRepository;

    @PostMapping
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder) {
        // جلب الحدث باستخدام الـ id
        Event event = eventRepository.findById(folder.getEvent().getId()).orElseThrow(() -> new RuntimeException("Event not found"));
        
        // تعيين الحدث للفولدر
        folder.setEvent(event);

        // حفظ الفولدر في قاعدة البيانات
        Folder saved = folderRepository.save(folder);
        return ResponseEntity.ok(saved);
    }


    // Endpoint للحصول على جميع الفولدرات
    @GetMapping("/all")
    public ResponseEntity<List<Folder>> getAllFolders() {
        try {
            List<Folder> folders = folderRepository.findAll();
            return ResponseEntity.ok(folders); // إرسال قائمة الفولدرات
        } catch (Exception e) {
            // التعامل مع الأخطاء عند الحصول على الفولدرات
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    
}
