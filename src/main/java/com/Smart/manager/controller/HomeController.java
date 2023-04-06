package com.Smart.manager.controller;

import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Smart.manager.Dao.UserRepository;
import com.Smart.manager.entities.User;
import com.Smart.manager.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userrepo;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String register(@ModelAttribute("user") User user,
			@RequestParam(value = "agree", defaultValue = "false") boolean agree, 
			Model m, HttpSession session)

	{
		try {
			
		
			/*if(result.hasErrors()) {
				m.addAttribute("user",user);
				System.out.println(result.toString());
				return"signup";
			}*/
			user.setRole("USER");
			user.setEnabled(true);
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			System.out.println(user);
			System.out.println(agree);
			this.userrepo.save(user);
			m.addAttribute("user", new User());
			session.setAttribute("message",
					new Message("successfully registered!!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message",
					new Message("Something wents wrong!!" + e.getMessage(), "alert-danger"));
			return "signup";

		}
	}
	@GetMapping("/signin")
   public String formhandler() {
	   return"Login";
	

	}
}
