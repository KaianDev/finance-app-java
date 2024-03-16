package br.com.money.controller;

import br.com.money.model.Activity;
import br.com.money.model.User;
import br.com.money.repository.ActivityRepository;
import br.com.money.repository.UserRepository;
import br.com.money.service.PdfService;
import br.com.money.service.TokenConvert;
import br.com.money.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenConvert tokenConvert;

    @GetMapping("/getPdf")
    public ResponseEntity<InputStreamResource> activityReport(HttpServletRequest request) throws IOException {
        var email = this.tokenService.getSubject(this.tokenConvert.convert(request));
        User user = this.userRepository.findByEmail(email);
        List<Activity> activities = this.activityRepository.findAllActivitiesByUser(user);
        ByteArrayInputStream bis = this.pdfService.activityPDFReport(activities, request);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline; filename=activities.pdf");
        return ResponseEntity.ok().header(String.valueOf(httpHeaders)).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }
}
