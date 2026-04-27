/**
 * Copyright (c) 2024 Sami Menik, PhD. All rights reserved.
 *
 *  *This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
 */
package movie.review.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import movie.review.app.models.Profile;
import movie.review.app.services.ProfileService;
import movie.review.app.services.UserService;

/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("profile_page");

        String currentUserId = userService.getLoggedInUser().getUserId();

        Profile profile = profileService.getProfile(currentUserId, currentUserId);

        mv.addObject("profile", profile);

        return mv;
    }

    @GetMapping("/{userId}")
    public ModelAndView userProfile(@PathVariable("userId") String userId,
            @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("profile_page");

        String currentUserId = userService.getLoggedInUser().getUserId();

        Profile profile = profileService.getProfile(currentUserId, userId);

        mv.addObject("profile", profile);

        return mv;
    }

}
