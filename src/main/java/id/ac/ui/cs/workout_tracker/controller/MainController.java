package id.ac.ui.cs.workout_tracker.controller;

import id.ac.ui.cs.workout_tracker.model.Events;


import id.ac.ui.cs.workout_tracker.service.Botservice;
import id.ac.ui.cs.workout_tracker.service.DatabaseService;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
public class MainController {

    @Autowired
    @Qualifier("lineSignatureValidator")
    private LineSignatureValidator lineSignatureValidator;

    @Autowired
    private Botservice botService;

    @Autowired
    private DatabaseService dbservice;



    /** Post Mapping.
     */
    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> callback(
            @RequestHeader("X-Line-Signature") String xlinesignature,
            @RequestBody String eventsPayload) {
        try {
           
            if (!lineSignatureValidator.validateSignature(
                    eventsPayload.getBytes(), xlinesignature)) {
                throw new RuntimeException("Invalid Signature Validation");
            }

            System.out.println(eventsPayload);
            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            Events eventsModel = objectMapper.readValue(eventsPayload, Events.class);

            eventsModel.getEvents().forEach((event) -> {
                if (event instanceof JoinEvent || event instanceof FollowEvent) {
                    String replyToken = ((ReplyEvent) event).getReplyToken();
                    botService.source = event.getSource();
                    botService.welcomeMessage(replyToken);
                } else if (event instanceof MessageEvent) {
                    botService.source = event.getSource();
                    botService.handleMessageEvent((MessageEvent) event);
                }
            });

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}


