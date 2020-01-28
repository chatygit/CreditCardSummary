package com.chaty.credit.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.chaty.credit.model.CreditFileModel;

public interface CreditFileRepo extends ElasticsearchRepository<CreditFileModel, String> {
	

}
