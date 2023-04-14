package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.BodyPostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.BodyPostingListRepository;
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
        return bodyPostingListRepository.save(bodyPostingList);
    }
}
