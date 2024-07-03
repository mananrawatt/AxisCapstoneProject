package com.weatherapp.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.weatherapp.models.HomeData;
import com.weatherapp.models.User;
import com.weatherapp.models.Weather;
import com.weatherapp.models.WeatherAppProperties;
import com.weatherapp.models.WeatherForecast;
import com.weatherapp.models.WeatherInfo;
import com.weatherapp.services.EmailService;
import com.weatherapp.services.UserService;
import com.weatherapp.services.WeatherService;

@RestController
public class HomeController {
	
	@Autowired WeatherService service;
	@Autowired UserService uservice;
	@Autowired EmailService eservice;
	@Autowired HttpSession session;
	
	@Autowired WeatherAppProperties props;
	
	@GetMapping("/")
	@CrossOrigin
	public String Homepage() {
		return "index";
	}
	
	@PostMapping("/login")
	@CrossOrigin
	public ResponseEntity<String> validate(@RequestParam String userid, @RequestParam String pwd) {
	    User user = uservice.validateUser(userid, pwd);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
	    }
	    return ResponseEntity.ok("Validation successful");
	}

	
	@GetMapping("/home")
	@CrossOrigin
	public ResponseEntity<HomeData> getHomePageData(HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    
	    String country = user.getCountry();
	    String city = user.getCity();
	    Weather weather = service.getWeather(country, city);
	    WeatherInfo winfo = new WeatherInfo(country, city, weather);
	    WeatherForecast wfinfo = service.getWeatherForecast(country, city);
	    HomeData homeData = new HomeData(winfo, wfinfo);
	    
	    return ResponseEntity.ok(homeData);
	}

	
	@GetMapping("/register")
	@CrossOrigin
	public ResponseEntity<String> getRegisterPage() {
	    return ResponseEntity.ok("Register page");
	}
	
	@GetMapping("/notify/{userid}")
	@CrossOrigin
	public ResponseEntity<String> sendNotification(@PathVariable("userid") String userid, String weather) {
	    User nuser = uservice.findByUserid(userid);
	    final String message = "Hi ! " + nuser.getUname() + ",\n" +
	            "The city " + nuser.getCity() + " has " + weather + ".\n" +
	            "Be careful.\n";
	    eservice.sendMessage(userid, message);
	    return ResponseEntity.ok("Mail sent successfully");
	}

	
	@GetMapping("/users")
	@CrossOrigin
	public ResponseEntity<List<User>> getUsersList() {
	    List<User> userList = new ArrayList<>();
	    for (User user : uservice.allUsers()) {
	        Weather weather = service.getWeather(user.getCountry(), user.getCity());
	        WeatherInfo winfo = new WeatherInfo(user.getCountry(), user.getCity(), weather);
	        user.setWeather(winfo.getDescription());
	        userList.add(user);
	    }
	    return ResponseEntity.ok(userList);
	}

	
	@GetMapping("/logout")
	public String Logout() {
		session.invalidate();
		return "redirect:/";
	}
	
	@PostMapping("/register")
	@CrossOrigin
	public ResponseEntity<String> registerProcess(@RequestBody User user) {
	    uservice.saveUser(user);
	    return ResponseEntity.ok("User registered successfully");
	}


	@GetMapping("/current")
	@CrossOrigin
	public ResponseEntity<WeatherInfo> getCurrentWeather(String country, String city) {
	    WeatherInfo weatherInfo = null;
	    if (country != null) {
	        Weather weather = service.getWeather(country, city);
	        weatherInfo = new WeatherInfo(country, city, weather);
	    }
	    if (weatherInfo != null) {
	        return ResponseEntity.ok(weatherInfo);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	
	@GetMapping("/forecast")
	@CrossOrigin
	public ResponseEntity<WeatherForecast> getForecastPage(String country, String city) {
	    if (country != null) {
	        WeatherForecast wfinfo = service.getWeatherForecast(country, city);
	        return ResponseEntity.ok(wfinfo);
	    }
	    return ResponseEntity.notFound().build();
	}
	
}
