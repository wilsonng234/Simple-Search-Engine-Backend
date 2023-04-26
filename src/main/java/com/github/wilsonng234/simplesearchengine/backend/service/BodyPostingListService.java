package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.BodyPostingList;
import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import com.github.wilsonng234.simplesearchengine.backend.repository.BodyPostingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Scope("prototype")
public class BodyPostingListService extends PostingListService {
    @Autowired
    private BodyPostingListRepository bodyPostingListRepository;
    @Autowired
    private PostingService postingService;

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

        List<String> postingIds = bodyPostingList.getPostingIds();
        List<String> filteredPostings = postingIds.stream().filter(p -> p.equals(posting.getPostingId())).toList();

        if (filteredPostings.size() == 0) {
            postingIds.add(posting.getPostingId());
        } else {
            postingService.getPosting(filteredPostings.get(0)).get().setWordPositions(posting.getWordPositions());
        }

        bodyPostingList.setPostingIds(postingIds);
        bodyPostingList.setMaxTF(Math.max(bodyPostingList.getMaxTF(), posting.getWordPositions().size()));
        return bodyPostingListRepository.save(bodyPostingList);
    }

    @Override
    public BodyPostingList getPostingList(String wordId) {
        Optional<BodyPostingList> bodyPostingListOptional = bodyPostingListRepository.findBodyPostingListByWordId(wordId);
        return bodyPostingListOptional.orElseGet(() -> createPostingList(wordId));
    }
}
