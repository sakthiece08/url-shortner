package com.teqmonic.urlshortner.controller;

import com.teqmonic.urlshortner.configs.ApplicationProperties;
import com.teqmonic.urlshortner.exception.ShortUrlNotFoundException;
import com.teqmonic.urlshortner.model.CreateShortUrlCmd;
import com.teqmonic.urlshortner.model.CreateShortUrlForm;
import com.teqmonic.urlshortner.model.ShortUrlDto;
import com.teqmonic.urlshortner.service.ShortUrlService;
import com.teqmonic.urlshortner.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class HomeController {

   private final ShortUrlService shortUrlService;
   private final SecurityUtil securityUtil;
   private final ApplicationProperties properties;

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "1", required = false) int pageNo, Model model) {
        List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls(pageNo);
        model.addAttribute("title", "URL Shortener - Thymeleaf");
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", "http://localhost:8090");
        model.addAttribute("createShortUrlForm", new CreateShortUrlForm("", false, 0L));
        return "index";
    }

    @PostMapping("/short-urls")
    String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                          BindingResult bindingResult, //binding result should be immediately after the @Valid parameter
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if(bindingResult.hasErrors()) {
            List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls(1);
            model.addAttribute("shortUrls", shortUrls);
            model.addAttribute("baseUrl", properties.baseUrl());
            return "index";
        }

        try {
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(form.originalUrl(), form.isPrivate(), form.expirationInDays(), securityUtil.getCurrentUserName());
            var shortUrlDto = shortUrlService.createShortUrl(cmd);
            redirectAttributes.addFlashAttribute("successMessage", "Short URL created successfully "+
                    properties.baseUrl()+"/s/"+shortUrlDto.shortKey());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create short URL");

        }
        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey) {
        Optional<ShortUrlDto> shortUrlDtoOptional = shortUrlService.accessShortUrl(shortKey, securityUtil.getCurrentUserName());
        if(shortUrlDtoOptional.isEmpty()) {
            throw new ShortUrlNotFoundException("Invalid short key: "+shortKey);
        }
        ShortUrlDto shortUrlDto = shortUrlDtoOptional.get();
        return "redirect:"+shortUrlDto.originalUrl();
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }
}
