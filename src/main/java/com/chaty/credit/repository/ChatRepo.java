package com.chaty.credit.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.chaty.credit.model.ChatModel;

public interface ChatRepo extends ElasticsearchRepository<ChatModel, String> {
	

}
