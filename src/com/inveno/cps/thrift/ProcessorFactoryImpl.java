package com.inveno.cps.thrift;

import org.apache.thrift.TMultiplexedProcessor;

import com.inveno.cps.authenticate.thrift.AuthenticateService;
import com.inveno.cps.authority.thrift.AuthorityService;
import com.inveno.cps.dictionary.thrift.DictionaryService;
import com.inveno.cps.review.thrift.ReviewService;

public class ProcessorFactoryImpl implements ProcessorFactory {
	private AuthenticateService.Iface authenticateService;
	private AuthorityService.Iface authorityService;
	private ReviewService.Iface reviewService;
	private DictionaryService.Iface dictionaryService;
	
	public DictionaryService.Iface getDictionaryService() {
		return dictionaryService;
	}
	public void setDictionaryService(DictionaryService.Iface dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	public ReviewService.Iface getReviewService() {
		return reviewService;
	}
	public void setReviewService(ReviewService.Iface reviewService) {
		this.reviewService = reviewService;
	}
	public AuthorityService.Iface getAuthorityService() {
		return authorityService;
	}
	public void setAuthorityService(AuthorityService.Iface authorityService) {
		this.authorityService = authorityService;
	}
	public AuthenticateService.Iface getAuthenticateService() {
		return authenticateService;
	}
	public void setAuthenticateService(AuthenticateService.Iface authenticateService) {
		this.authenticateService = authenticateService;
	}
	
	@Override
	public TMultiplexedProcessor getProcessor() {
		
		TMultiplexedProcessor processor = new TMultiplexedProcessor();
		
		AuthenticateService.Processor<AuthenticateService.Iface> authenticateProcess = new AuthenticateService.Processor<AuthenticateService.Iface>(authenticateService);
		AuthorityService.Processor<AuthorityService.Iface> authorityProcess = new AuthorityService.Processor<AuthorityService.Iface>(authorityService);
		ReviewService.Processor<ReviewService.Iface> reviewProcess = new ReviewService.Processor<ReviewService.Iface>(reviewService);
		DictionaryService.Processor<DictionaryService.Iface> dictionaryProcess = new DictionaryService.Processor<DictionaryService.Iface>(dictionaryService);
		processor.registerProcessor("AuthenticateService", authenticateProcess);
		processor.registerProcessor("AuthorityService", authorityProcess);
		processor.registerProcessor("ReviewService", reviewProcess);
		processor.registerProcessor("DictionaryService", dictionaryProcess);
		
		return processor;
		
	}

}
