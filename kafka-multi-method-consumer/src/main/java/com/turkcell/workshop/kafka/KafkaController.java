package com.turkcell.workshop.kafka;

import com.turkcell.workshop.kafka.dto.IntegerMessage;
import com.turkcell.workshop.kafka.dto.StringMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("multi-method")
public class KafkaController {
    @Autowired
    private KafkaTemplate<Object, Object> template;

    @PostMapping(path = "/send/integer/{what}")
    public void sendIntegerMessage(@PathVariable String what) {
        this.template.send("itopic", new IntegerMessage(what));
    }

    @PostMapping(path = "/send/string/{what}")
    public void sendStringMessage(@PathVariable String what) {
        this.template.send("stopic", new StringMessage(what));
    }

    @PostMapping(path = "/send/unknown/{what}")
    public void sendUnknown(@PathVariable String what) {
        this.template.send("stopic", what);
    }

}