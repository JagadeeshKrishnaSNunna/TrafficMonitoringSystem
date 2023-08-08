package com.rest.api.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rest.api.services.EmailService;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.api.PublishAck;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import io.nats.client.support.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.json.Json;
import javax.ws.rs.core.Application;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    @Autowired
    EmailService emailService;

    @RequestMapping(value = "/flood",method = RequestMethod.GET)
    public List<String> flood(){
        List<String> load=new ArrayList<>();
        try(Connection nc= Nats.connect()){
            JetStreamManagement jsm=nc.jetStreamManagement();
            StreamConfiguration streamConfig = StreamConfiguration.builder()
                    .name("hello")
                    .subjects("com.nats")
                    .storageType(StorageType.Memory)
                    .build();
            StreamInfo streamInfo = jsm.addStream(streamConfig);
//            System.out.println(streamInfo);
//            System.out.println(streamInfo);
            System.out.println(nc.getStatus());
            JetStream js = nc.jetStream();
            String str="";
            for(int i=1;i<17;i+=1) {
                str += "{\"license\":" + "\"PLTNO" + i + "\",\"colour\":\"red\",\"model\":\"BMW\",\"location\":\"39th Street,BroadWay BLVD,64111\",\"camID\":\"cam001\"}";
                PublishAck ack = js.publish("com.nats", str.getBytes());
                load.add(str);
                str = "";
                str += "{\"license\":" + "\"F1TOH" + (i - 1) + "\",\"colour\":\"white\",\"model\":\"RAM\",\"location\":\"47th Street,Mainstreet,64111\",\"camID\":\"cam02\"}";
                ack = js.publish("com.nats", str.getBytes());
                load.add(str);
                str = "";
                str += "{\"license\":" + "\"BHNO" + (i * 2 + 10 - 1) + "\",\"colour\":\"grey\",\"model\":\"Ford\",\"location\":\"12th grand,64111\",\"camID\":\"cam003\"}";
                ack = js.publish("com.nats", str.getBytes());
                load.add(str);
                str = "";
                str += "{\"license\":" + "\"XANO" + (i * 2 + 20 - 1) + "\",\"colour\":\"blue\",\"model\":\"Kia\",\"location\":\"37th,hyde park,64111\",\"camID\":\"cam004\"}";
                ack = js.publish("com.nats", str.getBytes());
                load.add(str);
                str = "";
                str += "{\"license\":" + "\"APNO" + (i * 2 + 30 - 1) + "\",\"colour\":\"red\",\"model\":\"Toyota\",\"location\":\"51th Troost,64111\",\"camID\":\"cam005\"}";
                ack = js.publish("com.nats", str.getBytes());
                load.add(str);
                str = "";
                str += "{\"license\":" + "\"BBNO" + (i * 2 + 40 - 1) + "\",\"colour\":\"black\",\"model\":\"Mazda\",\"location\":\"21th GillamHall,64111\",\"camID\":\"cam005\"}";
                ack = js.publish("com.nats", str.getBytes());
                load.add(str);
                str = "";
            }
        }catch (Exception e){e.printStackTrace();}
        return load;
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String test(){
        final String url= "https://my-deployment-315ae5.es.us-central1.gcp.cloud.es.io:9243/my_index";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("elastic","EIIqQKfCtNLSsnOHJtHuxDw1"));
        String result = restTemplate.getForObject(url, String.class);
        return result;
    }
@RequestMapping(value = "/notify",method = RequestMethod.GET)
    public String notify(@RequestParam String data){
        return emailService.sendEmail("jk.nunna09@gmail.com","Vehical Found Alert",data);
}

}


