package br.com.money.controller;

import br.com.money.model.Activity;
import br.com.money.repository.ActivityRepository;
import br.com.money.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/getPdf")
    public ResponseEntity<InputStreamResource> activityReport() throws IOException {
        List<Activity> activities = this.activityRepository.findAll();
        ByteArrayInputStream bis = this.pdfService.activityPDFReport(activities);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline; filename=activities.pdf");
        return ResponseEntity.ok().header(String.valueOf(httpHeaders)).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }
}
