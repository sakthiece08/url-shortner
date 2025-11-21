package com.teqmonic.urlshortner.controller;

import com.teqmonic.urlshortner.model.entities.ShortUrlEntity;
import com.teqmonic.urlshortner.repository.ShortUrlRepository;
import com.teqmonic.urlshortner.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class HomeController {

   private final ShortUrlService shortUrlService;

    @GetMapping("/")
    public String home(Model model) {
        List<ShortUrlEntity> shortUrls = shortUrlService.findPublicShortUrls();
        model.addAttribute("title", "URL Shortener - Thymeleaf");
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", "http://localhost:8090");
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
