package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.repository.ParentLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ParentLinkService {
    @Autowired
    ParentLinkRepository parentLinkRepository;

    public ParentLink createParentLink(ParentLink parentLink) {
        List<String> parentLinks = parentLink.getParentUrls() == null ? new LinkedList<>() : parentLink.getParentUrls();

        return parentLinkRepository.insert(new ParentLink(parentLink.getUrl(), parentLinks));
    }
}
