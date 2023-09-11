package com.devidend01.service;

import com.devidend01.exception.impl.NoCompanyException;
import com.devidend01.model.Company;
import com.devidend01.model.ScrapedResult;
import com.devidend01.persist.CompanyRepository;
import com.devidend01.persist.DividendRepository;
import com.devidend01.persist.entitiy.CompanyEntity;
import com.devidend01.persist.entitiy.DividendEntity;
import com.devidend01.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service 
@AllArgsConstructor
public class CompanyService { //싱글톤으로 관리됨

    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker ->" + ticker); //이미 해당 회사 데이터가 있는 경우
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker ->" + ticker); //회사 정보가 존재하지 않을시 반환
        }

        // 해당 회사 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company); //scrapedResult가 회사 정보를 받는다
        
        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                                                         .map(e -> new DividendEntity(companyEntity.getId(), e)) //diviend 인스턴스 가 DividendEntity 되도록 처리
                                                         .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities); //얻은 배당금 값을 dividendRepository에 저장한다

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                               .map(e -> e.getName())
                               .collect(Collectors.toList());
    }

    //자동완성 데이터 저장
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null); //key_value 값을 따로 부여하지 않고 자동완성만 구현, 회사명만 저장
    }

    //자동완성 데이터 검색
    public List<String> autoComplete(String keyword) {
        return(List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .collect(Collectors.toList());
    }

    //자동완성 데이터 삭제
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());
        return company.getName();
    }
}
