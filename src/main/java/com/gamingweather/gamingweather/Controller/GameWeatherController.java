package com.gamingweather.gamingweather.Controller;

import com.gamingweather.gamingweather.Domain.City;
import com.gamingweather.gamingweather.Domain.GWUser;
import com.gamingweather.gamingweather.Domain.RegistrationDetails;
import com.gamingweather.gamingweather.Repository.CityRepository;
import com.gamingweather.gamingweather.Repository.GWUserRepository;
import com.gamingweather.gamingweather.Repository.RegistrationDetailsRepository;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Set;

@Controller
public class GameWeatherController {

    @Autowired
    CityRepository cityRepository;

    @Autowired
    GWUserRepository gwUserRepository;

    @Autowired
    RegistrationDetailsRepository registrationDetailsRepository;

    @GetMapping("/")
    public String indexPage(){
        return "redirect:login";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/gamingWeather")
    public String gamingWeatherPage(Model model,HttpServletRequest request){
        GWUser gwUser = gwUserRepository.findByUsername(request.getRemoteUser());
        model.addAttribute("cities",gwUser.getCities());
        return "gamingWeather";
    }

    @PostMapping("/addCity")
    @ResponseBody
    public String addCity(HttpServletRequest request,@RequestParam String city, Model model) throws IOException {
        //Check if city can be found at OpenWeatherAPI
        if(checkIfCityIsNotFound(city)){
            String wrongCity = "We can not find your city. Please try again.";
            model.addAttribute("wrongCity",wrongCity);
            return "registration";
        }

        GWUser gwUser = gwUserRepository.findByUsername(request.getRemoteUser());

        addNewCityToUser(city,gwUser,cityRepository);

        gwUserRepository.save(gwUser);

        model.addAttribute("cities",gwUser.getCities());
        return "gamingWeather";
    }

    @GetMapping("/registration")
    public ModelAndView registrationPage(){
        return new ModelAndView("registration").addObject("GWUser",new GWUser());
    }

    @PostMapping("/registration")
    public ModelAndView submitForm(@Valid GWUser gwUser, BindingResult bindingResult, @RequestParam String city) throws IOException {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("registration").addObject("GWUser",gwUser);
        }

        //Check if city can be found at OpenWeatherAPI
        if(checkIfCityIsNotFound(city)){
            String wrongCity = "We can not find your city. Please try again.";
            return new ModelAndView("registration").addObject("wrongCity",wrongCity);
        }

        //add validation for username and email to be unique.
        GWUser gwUser1 = gwUserRepository.findByUsername(gwUser.getUsername());
        GWUser gwUser2 = gwUserRepository.findByEmail(gwUser.getEmail());
        if(gwUser1 != null || gwUser2 != null){
            String ifOccupiedMessage = "Sorry, that username and/or email is occupied. Please try again.";
            return new ModelAndView("registration").addObject("GWUser",new GWUser())
                    .addObject("ifOccupiedMessage",ifOccupiedMessage);
        }

        //send in password and encode it
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        gwUser.setPassword(passwordEncoder.encode(gwUser.getPassword()));

        //Save values for a new user
        Date dateNow = new Date();
        RegistrationDetails registrationDetails = new RegistrationDetails(dateNow,dateNow);
        registrationDetailsRepository.save(registrationDetails);
        RegistrationDetails registrationDetails2 = registrationDetailsRepository.findByRegistrationTime(dateNow);
        gwUser.setRegistrationDetails(registrationDetails2);

        addNewCityToUser(city,gwUser,cityRepository);

        gwUserRepository.save(gwUser);

        String greeting = "Thank you "+ gwUser.getUsername() +" for your registration";
        return new ModelAndView("login")
                .addObject("greeting",greeting);
    }

    @GetMapping("/getCityName")
    @ResponseBody
    public String getCityName(HttpServletRequest request){

        GWUser gwUser = gwUserRepository.findByUsername(request.getRemoteUser());
        Set<City> cities = gwUser.getCities();
        String cityName = "" + cities.iterator().next().getCityName().trim();

        return cityName;
    }

    public static void addNewCityToUser(String city, GWUser gwUser,CityRepository cityRepository){
        City city1 = cityRepository.findByCityName(city);
        if(city1 != null){
            gwUser.addCity(city1);
        } else {
            City city2 = new City(city);
            cityRepository.save(city2);
            City city3 = cityRepository.findByCityName(city);
            gwUser.addCity(city3);
        }
    }

    public static boolean checkIfCityIsNotFound(String city) throws IOException {
        String responseFromAPI = GameWeatherController.sendRequest("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&APPID=*********************");
        return responseFromAPI.contains("Not Found");
    }

    public static String sendRequest(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
// The underlying HTTP connection is still held by the response object
// to allow the response content to be streamed directly from the network socket.
// In order to ensure correct deallocation of system resources
// the user MUST call CloseableHttpResponse#close() from a finally clause.
// Please note that if response content is not fully consumed the underlying
// connection cannot be safely re-used and will be shut down and discarded
// by the connection manager.
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(entity1.getContent(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null){
                System.out.println(inputStr);
                responseStrBuilder.append(inputStr);
            }
            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
            System.out.println(jsonObject.get("name"));

            // ensures it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return response1.getStatusLine().toString();
    }

}
