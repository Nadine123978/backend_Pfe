//package com.itbulls.nadine.spring.springbootdemo.seed;
//
//import com.itbulls.nadine.spring.springbootdemo.model.*;
//import com.itbulls.nadine.spring.springbootdemo.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    @Autowired private EventRepository eventRepository;
//    @Autowired private SectionRepository sectionRepository;
//    @Autowired private SeatRepository seatRepository;
//    @Autowired private CategoryRepository categoryRepository;
//    @Autowired private LocationRepository locationRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (eventRepository.count() > 0) return; // منع التكرار
//
//        // 1. Category و Location (ضروري يكونوا موجودين)
//        Category category = categoryRepository.findAll().stream().findFirst().orElse(null);
//        Location location = locationRepository.findAll().stream().findFirst().orElse(null);
//        if (category == null || location == null) {
//            System.out.println("❌ أضف Category و Location قبل تشغيل Seeder!");
//            return;
//        }
//
//        // 2. إنشاء Event
//        Event event = new Event();
//        event.setTitle("Test Event 🎤");
//        event.setDescription("This is a sample event for testing.");
//        event.setStartDate(LocalDateTime.now().plusDays(5));
//        event.setEndDate(LocalDateTime.now().plusDays(6));
//        event.setCategory(category);
//        event.setLocation(location);
//        event.setTotalTickets(90);
//        event.setSoldTickets(0);
//        event.setStatus("upcoming");
//        event.setImageUrl("https://via.placeholder.com/400x200.png");
//        event.setIsFeatured(true);
//        eventRepository.save(event);
//
//        // 3. إضافة 3 أقسام
//        String[] sectionNames = {"A", "B", "C"};
//        String[] colors = {"#FFA07A", "#87CEFA", "#90EE90"};
//
//        for (int i = 0; i < sectionNames.length; i++) {
//            Section section = new Section();
//            section.setName("Section " + sectionNames[i]);
//            section.setColor(colors[i]);
//            section.setEvent(event);
//            section.setPrice(30.0 + (i * 10));
//            sectionRepository.save(section);
//
//            // 4. إنشاء 30 كرسي داخل كل قسم
//            for (int j = 1; j <= 30; j++) {
//                Seat seat = new Seat();
//                seat.setCode(sectionNames[i] + "-" + j);
//                seat.setReserved(false);
//                seat.setSection(section);
//                seatRepository.save(seat);
//            }
//        }
//
//        System.out.println("✅ بيانات تجريبية تم إضافتها بنجاح!");
//    }
//}
