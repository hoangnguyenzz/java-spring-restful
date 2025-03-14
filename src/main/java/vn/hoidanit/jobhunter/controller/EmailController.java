package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
        this.emailService = emailService;
    }

    @GetMapping()

    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailFromTemplateSync("nguyenvanhoang2004bn@gmail.com",
        // "Testing from Spring Boot",
        // "job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }
}