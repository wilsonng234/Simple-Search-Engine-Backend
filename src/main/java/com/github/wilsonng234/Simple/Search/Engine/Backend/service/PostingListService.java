package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.PostingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

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
}
