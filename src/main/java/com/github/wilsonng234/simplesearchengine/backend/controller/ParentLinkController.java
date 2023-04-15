package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.service.ParentLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/parentLinks")
public class ParentLinkController {
    @Autowired
    private ParentLinkService parentLinkService;

    @GetMapping
    public ResponseEntity<List<ParentLink>> getAllParentLinks() {
        return new ResponseEntity<>(parentLinkService.allParentLinks(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParentLink> createParentLink(@RequestBody ParentLink parentLink) {
        return new ResponseEntity<>(parentLinkService.createParentLink(parentLink), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ParentLink> putParentLink(@RequestBody ParentLink parentLink) {
        return new ResponseEntity<>(parentLinkService.putParentLink(parentLink), HttpStatus.OK);
    }
}
