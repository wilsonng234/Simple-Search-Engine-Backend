package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.PostingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PostingListService {
    @Autowired
    private PostingListRepository postingListRepository;

    public List<PostingList> allPostingLists() {
        return postingListRepository.findAll();
    }

    public PostingList createPostingList(String wordId) {
        PostingList postingList = new PostingList(wordId, new LinkedList<>());

        return postingListRepository.insert(postingList);
    }

    public PostingList putPositingList(String wordId, Posting posting) {
        Optional<PostingList> postingListOptional = postingListRepository.findPostingListByWordId(wordId);
        PostingList postingList = postingListOptional.orElseGet(() -> createPostingList(wordId));

        List<Posting> postings = postingList.getPostings();
        List<Posting> filteredPostings = postings.stream().filter(p -> p.equals(posting)).toList();

        if (filteredPostings.size() == 0) {
            postings.add(posting);
        } else {
            filteredPostings.get(0).setWordPositions(posting.getWordPositions());
        }

        postingList.setPostings(postings);
        return postingListRepository.save(postingList);
    }
}
