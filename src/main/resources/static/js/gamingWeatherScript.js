$(document).ready(function () {

    var city = "Uppsala";

    $.ajax({
        type: "GET",
        url: "/getCityName", //which is mapped to its partner function on our controller class
        success: function (result) {
            city = result;

            requestToAPI(city);

        }
    });

});

$("#addCityButton").click(function(e) {
    e.preventDefault();
    var city = $("#writtenCity").val();
    $.ajax({
        type: "POST",
        data: {
            city:city
        },
        url: "/addCity", //which is mapped to its partner function on our controller class
        success: function (){
            location.reload()
        }
    });
    console.log();
});

//cityName is the whole tag and you need to access the id of that tag
function changeCity(cityName) {
    var city = $(cityName).attr("id");

    $( "#city > span" ).remove();
    $( "#gamingWeatherMessage > div" ).remove();

    requestToAPI(city);
}

var requestToAPI = function(city){
    //AJAX request to openweather api
    $.getJSON("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&APPID=********************", function (data) {

        var rawJson = JSON.stringify(data);
        var json = JSON.parse(rawJson);

        $("#city").append("<span>" + json.name + "</span>");

        var timeForSunset = getTimeForSunset(json);
        var timeForSunrise = getTimeForSunrise(json);
        var timeNow = new Date(Date.now()).toLocaleTimeString();

        checkIfGamingWeather(json, timeNow, timeForSunrise, timeForSunset);

    });
}

var checkIfGamingWeather = function (json, timeNow, timeForSunrise, timeForSunset) {
    //Check for clouds, rain, snow, temp and if sun is up

    var description = json.weather[0].description;

    var badWeatherWords = ["thunderstorm", "rain", "snow",
        "drizzle", "fog", "mist", "haze"];

    var goodWeather = true;

    for (var i = 0; i < badWeatherWords.length; i++) {
        if (description.includes(badWeatherWords[i])) {
            goodWeather = false;
            break;
        }
    }

    if (goodWeather &&
        timeNow < timeForSunset &&
        timeNow > timeForSunrise &&
        json.main.temp > 20 &&
        json.clouds.all < 25) {
        return $("#gamingWeatherMessage").append("<div id='noGW'>It´s NOT Gaming Weather</div>");
    } else {
        return $("#gamingWeatherMessage").append("<div id='GW'>It´s Gaming Weather</div>");
    }
};

var getTimeForSunset = function (json) {
    var secondsForSunset = json.sys.sunset;
    var date = new Date(secondsForSunset * 1000);
    return date.toLocaleTimeString();
};

var getTimeForSunrise = function (json) {
    var secondsForSunrise = json.sys.sunrise;
    var date = new Date(secondsForSunrise * 1000);
    return date.toLocaleTimeString();
};
