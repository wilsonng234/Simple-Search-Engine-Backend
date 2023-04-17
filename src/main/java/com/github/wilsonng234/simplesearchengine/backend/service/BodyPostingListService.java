package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.BodyPostingList;
import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import com.github.wilsonng234.simplesearchengine.backend.repository.BodyPostingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class BodyPostingListService extends PostingListService {
    @Autowired
    private BodyPostingListRepository bodyPostingListRepository;

    @Override
    public List<? extends PostingList> allPostingLists() {
        return bodyPostingListRepository.findAll();
    }

    @Override
    public BodyPostingList createPostingList(String wordId) {
        return bodyPostingListRepository.insert(new BodyPostingList(wordId, new LinkedList<>()));
    }

    @Override
    public BodyPostingList putPositingList(String wordId, Posting posting) {
        Optional<BodyPostingList> bodyPostingListOptional = bodyPostingListRepository.findBodyPostingListByWordId(wordId);
        BodyPostingList bodyPostingList = bodyPostingListOptional.orElseGet(() -> createPostingList(wordId));

        List<Posting> postings = bodyPostingList.getPostings();
        List<Posting> filteredPostings = postings.stream().filter(p -> p.equals(posting)).toList();

        if (filteredPostings.size() == 0) {
            postings.add(posting);
        } else {
            filteredPostings.get(0).setWordPositions(posting.getWordPositions());
        }

        bodyPostingList.setPostings(postings);
        bodyPostingList.setMaxTF(Math.max(bodyPostingList.getMaxTF(), posting.getWordPositions().size()));
        return bodyPostingListRepository.save(bodyPostingList);
    }

    @Override
    public BodyPostingList getPostingList(String wordId) {
        Optional<BodyPostingList> bodyPostingListOptional = bodyPostingListRepository.findBodyPostingListByWordId(wordId);
        return bodyPostingListOptional.orElseGet(() -> createPostingList(wordId));
    }
}
