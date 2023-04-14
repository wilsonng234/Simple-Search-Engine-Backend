package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class PostingListService {
    public abstract List<? extends PostingList> allPostingLists();

    public abstract PostingList createPostingList(String wordId);

    public abstract PostingList putPositingList(String wordId, Posting posting);
}
