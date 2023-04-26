package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import com.github.wilsonng234.simplesearchengine.backend.model.TitlePostingList;
import com.github.wilsonng234.simplesearchengine.backend.repository.TitlePostingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Scope("prototype")
public class TitlePostingListService extends PostingListService {
    @Autowired
    private TitlePostingListRepository titlePostingListRepository;
    @Autowired
    private PostingService postingService;

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

        List<String> postingIds = titlePostingList.getPostingIds();
        List<String> filteredPostings = postingIds.stream().filter(p -> p.equals(posting.getPostingId())).toList();

        if (filteredPostings.size() == 0) {
            postingIds.add(posting.getPostingId());
        } else {
            postingService.getPosting(filteredPostings.get(0)).get().setWordPositions(posting.getWordPositions());
        }

        titlePostingList.setPostingIds(postingIds);
        titlePostingList.setMaxTF(Math.max(titlePostingList.getMaxTF(), posting.getWordPositions().size()));
        return titlePostingListRepository.save(titlePostingList);
    }

    @Override
    public TitlePostingList getPostingList(String wordId) {
        Optional<TitlePostingList> titlePostingListOptional = titlePostingListRepository.findTitlePostingListByWordId(wordId);
        return titlePostingListOptional.orElseGet(() -> createPostingList(wordId));
    }
}
