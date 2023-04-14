package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.TitlePostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.TitlePostingListRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TitlePostingListService extends PostingListService {
    @Autowired
    private TitlePostingListRepository titlePostingListRepository;

    @Override
    public List<? extends PostingList> allPostingLists() {
        return titlePostingListRepository.findAll();
    }

    @Override
    public TitlePostingList createPostingList(String wordId) {
        return titlePostingListRepository.insert(new TitlePostingList(wordId, new LinkedList<>()));
    }

    @Override
    public TitlePostingList putPositingList(String wordId, Posting posting) {
        Optional<TitlePostingList> titlePostingListOptional = titlePostingListRepository.findTitlePostingListByWordId(wordId);
        TitlePostingList titlePostingList = titlePostingListOptional.orElseGet(() -> createPostingList(wordId));

        List<Posting> postings = titlePostingList.getPostings();
        List<Posting> filteredPostings = postings.stream().filter(p -> p.equals(posting)).toList();

        if (filteredPostings.size() == 0) {
            postings.add(posting);
        } else {
            filteredPostings.get(0).setWordPositions(posting.getWordPositions());
        }

        titlePostingList.setPostings(postings);
        return titlePostingListRepository.save(titlePostingList);
    }
}
