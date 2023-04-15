package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.repository.ParentLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ParentLinkService {
    @Autowired
    ParentLinkRepository parentLinkRepository;

    public ParentLink createParentLink(ParentLink parentLink) {
        Set<String> parentLinks = parentLink.getParentUrls() == null ? new HashSet<>() : parentLink.getParentUrls();

        return parentLinkRepository.insert(new ParentLink(parentLink.getUrl(), parentLinks));
    }

    public ParentLink putParentLink(ParentLink parentLink) {
        ParentLink existingParentLink = parentLinkRepository.findById(parentLink.getUrl()).orElseGet(() -> createParentLink(parentLink));

        Set<String> parentUrls = parentLink.getParentUrls();
        if (parentUrls != null)
            existingParentLink.getParentUrls().addAll(parentUrls);
        parentLink.setParentUrls(parentUrls);

        return parentLinkRepository.save(parentLink);
    }

    public List<ParentLink> allParentLinks() {
        return parentLinkRepository.findAll();
    }
}
