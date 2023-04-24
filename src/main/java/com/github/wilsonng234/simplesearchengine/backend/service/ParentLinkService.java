package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.repository.ParentLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Scope("prototype")
public class ParentLinkService {
    @Autowired
    ParentLinkRepository parentLinkRepository;

    public ParentLink createParentLinks(ParentLink parentLink) {
        Set<String> parentLinks = parentLink.getParentUrls() == null ? new HashSet<>() : parentLink.getParentUrls();

        return parentLinkRepository.insert(new ParentLink(parentLink.getUrl(), parentLinks));
    }

    public ParentLink putParentLinks(ParentLink parentLink) {
        ParentLink existingParentLink = parentLinkRepository.findById(parentLink.getUrl()).orElseGet(() -> createParentLinks(parentLink));

        Set<String> parentUrls = parentLink.getParentUrls();
        if (parentUrls != null)
            parentUrls.addAll(existingParentLink.getParentUrls());
        parentLink.setParentUrls(parentUrls);

        return parentLinkRepository.save(parentLink);
    }

    public Optional<ParentLink> getParentLinks(String url) {
        return parentLinkRepository.findParentLinkByUrl(url);
    }

    public List<ParentLink> allParentLinks() {
        return parentLinkRepository.findAll();
    }
}
