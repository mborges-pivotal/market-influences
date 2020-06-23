package com.borgescloud.markets.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.borgescloud.markets.news.service.FeedService;
import com.borgescloud.markets.news.service.NewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController extends BaseController {

    @Value("${spring.application.name}")
    private String applicationName; 

    private FeedService feedSrvc;
    private NewsService newsSrvc;

    @Autowired
    public HomeController (FeedService feedSrvc, NewsService newsSrvc) {
        this.feedSrvc = feedSrvc;
        this.newsSrvc = newsSrvc;
    }

    // Login form  
    @RequestMapping("/login.html")  
    public String login() {  
        return "login";  
    }  
    // Login form with error  
    @RequestMapping("/login-error.html")  
    public String loginError(Model model) {  
        model.addAttribute("loginError", true);  
        return "login";  
    }      

    @GetMapping("/")
    public String index(final Model model) {
        model.addAttribute("appName", applicationName);
        model.addAttribute("feedCount", feedSrvc.count());
        model.addAttribute("newsCount", newsSrvc.count());
        return "index";
    }

    @GetMapping("/logout.html")
    public String fetchSignoutSite(HttpServletRequest request, HttpServletResponse response) {        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
          
        return "redirect:/";
    }    
    
}