package com.itbulls.nadine.spring.springbootdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.GroupRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SectionRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;


@SpringBootApplication
@EnableScheduling
public class SpringbootdemoApplication {
    public static void main(String[] args) {
    	  System.out.println("🎯 APP STARTED");
        SpringApplication.run(SpringbootdemoApplication.class, args);
    }
	
	@Configuration
	public class WebConfig {
	    @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/")
	                        .allowedOrigins("http://localhost:5173") // ← Vite dev server
	                        .allowedMethods("*")
	                        .allowedHeaders("*");
	            }
	        };
	    }
	}
	
	@Configuration
	public class SeatInitConfig {

	    @Bean
	    public CommandLineRunner generateSeats(SeatRepository seatRepository, SectionRepository sectionRepository) {
	        return args -> {
	            Section section = sectionRepository.findById(1L).orElse(null); // choose the right section

	            if (section != null && seatRepository.findAll().isEmpty()) {
	                char[] rows = {'A', 'B', 'C', 'D', 'E'};
	                int seatsPerRow = 25;

	                for (char row : rows) {
	                    for (int i = 1; i <= seatsPerRow; i++) {
	                        Seat seat = new Seat();
	                        seat.setCode("" + row + i);
	                        seat.setReserved(false);
	                        seat.setSection(section);
	                        seatRepository.save(seat);
	                    }
	                }

	                System.out.println("✅ Seats generated successfully.");
	            }
	        };
	    }
	}
	@Bean
	CommandLineRunner run(UserRepository repo, PasswordEncoder encoder, GroupRepository groupRepository) {
	    return args -> {
	    	User existingUser = repo.findByEmail("superadmin@gmail.com");
	    	if (existingUser == null) {
	    		Group superAdminGroup = groupRepository.findByName("SUPER_ADMIN")
	    			    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN group not found"));


	            User user = new User();
	            user.setUsername("superadmin");
	            user.setEmail("superadmin@gmail.com");
	            user.setPassword(encoder.encode("superpass"));
	            user.setGroup(superAdminGroup);
	            user.setEnabled(true);
	            repo.save(user);
	            System.out.println("✅ Super admin created");
	        } else {
	            System.out.println("ℹ️ Super admin already exists");
	        }
	    };
	}

}