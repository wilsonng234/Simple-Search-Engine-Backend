package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.service.ParentLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/parentLinks")
@Scope("prototype")
public class ParentLinkController {
    @Autowired
    private ParentLinkService parentLinkService;

    @GetMapping
    public ResponseEntity<ParentLink> getParentLinks(@RequestParam String url) {
        Optional<ParentLink> parentLinks = parentLinkService.getParentLinks(url);

        if (parentLinks.isPresent())
            return new ResponseEntity<>(parentLinks.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<ParentLink> createParentLink(@RequestBody ParentLink parentLink) {
        return new ResponseEntity<>(parentLinkService.createParentLinks(parentLink), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ParentLink> putParentLink(@RequestBody ParentLink parentLink) {
        return new ResponseEntity<>(parentLinkService.putParentLinks(parentLink), HttpStatus.OK);
    }
}
