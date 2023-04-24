package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public abstract class PostingListService {
    public abstract List<? extends PostingList> allPostingLists();

    public abstract PostingList createPostingList(String wordId);

    public abstract PostingList putPositingList(String wordId, Posting posting);

    public abstract PostingList getPostingList(String wordId);
}
